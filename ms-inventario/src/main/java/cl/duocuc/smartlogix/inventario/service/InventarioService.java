package cl.duocuc.smartlogix.inventario.service;

import cl.duocuc.smartlogix.inventario.dto.ProductoDTO;
import cl.duocuc.smartlogix.inventario.model.Producto;
import cl.duocuc.smartlogix.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PATRÓN: Repository Pattern – la lógica de negocio se mantiene
 * separada del acceso a datos, usando el repositorio como abstracción.
 */
@Service
public class InventarioService {

    private final ProductoRepository productoRepository;

    public InventarioService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // ── Consultas ────────────────────────────────────────────────────────────

    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO obtenerPorId(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        return toDTO(p);
    }

    public ProductoDTO obtenerPorSku(String sku) {
        Producto p = productoRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con SKU: " + sku));
        return toDTO(p);
    }

    // ── Escritura ────────────────────────────────────────────────────────────

    @Transactional
    public ProductoDTO crear(ProductoDTO dto) {
        if (productoRepository.existsBySku(dto.getSku())) {
            throw new RuntimeException("Ya existe un producto con SKU: " + dto.getSku());
        }
        Producto p = toEntity(dto);
        return toDTO(productoRepository.save(p));
    }

    @Transactional
    public ProductoDTO actualizar(Long id, ProductoDTO dto) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        p.setNombre(dto.getNombre());
        p.setStock(dto.getStock());
        p.setPrecio(dto.getPrecio());
        p.setDescripcion(dto.getDescripcion());
        return toDTO(productoRepository.save(p));
    }

    @Transactional
    public ProductoDTO reducirStock(String sku, int cantidad) {
        int filasActualizadas = productoRepository.reducirStock(sku, cantidad);
        if (filasActualizadas == 0) {
            Producto p = productoRepository.findBySku(sku)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + sku));
            throw new RuntimeException("Stock insuficiente. Stock actual: " + p.getStock()
                    + " | Solicitado: " + cantidad);
        }
        return obtenerPorSku(sku);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private ProductoDTO toDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setSku(p.getSku());
        dto.setStock(p.getStock());
        dto.setPrecio(p.getPrecio());
        dto.setDescripcion(p.getDescripcion());
        dto.setFechaCreacion(p.getFechaCreacion() != null ? p.getFechaCreacion().toString() : null);
        dto.setFechaActualizacion(p.getFechaActualizacion() != null ? p.getFechaActualizacion().toString() : null);
        return dto;
    }

    private Producto toEntity(ProductoDTO dto) {
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setSku(dto.getSku());
        p.setStock(dto.getStock());
        p.setPrecio(dto.getPrecio());
        p.setDescripcion(dto.getDescripcion());
        return p;
    }
}
