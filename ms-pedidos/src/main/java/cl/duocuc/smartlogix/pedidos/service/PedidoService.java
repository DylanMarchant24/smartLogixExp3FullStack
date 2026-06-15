package cl.duocuc.smartlogix.pedidos.service;

import cl.duocuc.smartlogix.pedidos.dto.PedidoDTO;
import cl.duocuc.smartlogix.pedidos.factory.PedidoFactory;
import cl.duocuc.smartlogix.pedidos.model.EstadoPedido;
import cl.duocuc.smartlogix.pedidos.model.Pedido;
import cl.duocuc.smartlogix.pedidos.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio de Pedidos – integra tres patrones:
 *
 * 1. REPOSITORY PATTERN: usa PedidoRepository para persistir/consultar pedidos.
 * 2. FACTORY METHOD:     usa PedidoFactory para crear pedidos según su estado.
 * 3. CIRCUIT BREAKER:    implementación manual que evita cascada de fallos
 *                        cuando ms-inventario no responde.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final RestTemplate restTemplate;

    @Value("${inventario.url:http://localhost:8081}")
    private String inventarioUrl;

    // ── Circuit Breaker – estado interno ──────────────────────────────────────
    private boolean circuitAbierto = false;
    private long   ultimoFalloMs  = 0L;
    /** Tiempo de espera antes de intentar cerrar el circuito (30 s) */
    private static final long RESET_MS = 30_000L;

    public PedidoService(PedidoRepository pedidoRepository, RestTemplate restTemplate) {
        this.pedidoRepository = pedidoRepository;
        this.restTemplate     = restTemplate;
    }

    // ── Consultas ─────────────────────────────────────────────────────────────

    public List<PedidoDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PedidoDTO obtenerPorId(Long id) {
        return toDTO(pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id)));
    }

    public List<PedidoDTO> listarPorEmail(String email) {
        return pedidoRepository.findByClienteEmail(email).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<PedidoDTO> listarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    // ── Escritura ─────────────────────────────────────────────────────────────

    /**
     * Flujo principal de creación de pedido:
     *  1. Consulta stock en ms-inventario (con Circuit Breaker).
     *  2a. Si stock OK → Factory crea pedido APROBADO y descuenta stock.
     *  2b. Si circuit abierto → Factory crea pedido CREADO (fallback).
     *  2c. Si stock insuficiente → rechaza la solicitud.
     */
    @Transactional
    public PedidoDTO crearPedido(PedidoDTO dto) {
        Integer stock = consultarStockCB(dto.getSkuProducto());
        Pedido pedido;

        if (stock == null) {
            // Circuit abierto → fallback: registrar el pedido pendiente de revisión
            pedido = PedidoFactory.crearNuevo(
                    dto.getSkuProducto(), dto.getCantidad(), dto.getClienteEmail());
        } else if (stock >= dto.getCantidad()) {
            // Stock suficiente → aprobar y descontar
            pedido = PedidoFactory.crearAprobado(
                    dto.getSkuProducto(), dto.getCantidad(), dto.getClienteEmail(), stock);
            reducirStockCB(dto.getSkuProducto(), dto.getCantidad());
        } else {
            throw new RuntimeException(
                    "Stock insuficiente para SKU " + dto.getSkuProducto()
                    + ". Disponible: " + stock + " | Solicitado: " + dto.getCantidad());
        }

        return toDTO(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoDTO cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));
        pedido.setEstado(nuevoEstado);
        pedido.setFechaActualizacion(LocalDateTime.now());
        return toDTO(pedidoRepository.save(pedido));
    }

    // ── Circuit Breaker ───────────────────────────────────────────────────────

    /**
     * Consulta el stock de un producto en ms-inventario.
     * Si el circuito está abierto (o falla la llamada), retorna null (fallback).
     */
    private Integer consultarStockCB(String sku) {
        if (circuitoAbierto()) {
            System.out.println("[CircuitBreaker] ABIERTO – fallback para SKU: " + sku);
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> producto = restTemplate.getForObject(
                    inventarioUrl + "/api/inventario/sku/" + sku, Map.class);
            cerrarCircuito();
            return producto != null ? (Integer) producto.get("stock") : null;
        } catch (RestClientException e) {
            abrirCircuito();
            System.out.println("[CircuitBreaker] Fallo detectado → circuito ABIERTO: " + e.getMessage());
            return null;
        }
    }

    private void reducirStockCB(String sku, int cantidad) {
        if (circuitoAbierto()) return;
        try {
            restTemplate.patchForObject(
                    inventarioUrl + "/api/inventario/sku/" + sku + "/reducir",
                    Map.of("cantidad", cantidad),
                    Void.class);
        } catch (RestClientException e) {
            abrirCircuito();
        }
    }

    private boolean circuitoAbierto() {
        if (circuitAbierto) {
            if (System.currentTimeMillis() - ultimoFalloMs > RESET_MS) {
                circuitAbierto = false; // half-open: intentar una petición
                return false;
            }
            return true;
        }
        return false;
    }

    private void abrirCircuito() {
        circuitAbierto = true;
        ultimoFalloMs  = System.currentTimeMillis();
    }

    private void cerrarCircuito() {
        circuitAbierto = false;
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private PedidoDTO toDTO(Pedido p) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(p.getId());
        dto.setSkuProducto(p.getSkuProducto());
        dto.setCantidad(p.getCantidad());
        dto.setClienteEmail(p.getClienteEmail());
        dto.setEstado(p.getEstado() != null ? p.getEstado().name() : null);
        dto.setUltimoStockConocido(p.getUltimoStockConocido());
        dto.setFechaCreacion(p.getFechaCreacion() != null ? p.getFechaCreacion().toString() : null);
        dto.setFechaActualizacion(p.getFechaActualizacion() != null ? p.getFechaActualizacion().toString() : null);
        return dto;
    }
}
