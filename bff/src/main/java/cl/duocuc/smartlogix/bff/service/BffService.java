package cl.duocuc.smartlogix.bff.service;

import cl.duocuc.smartlogix.bff.dto.DashboardDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * PATRON: Backend For Frontend (BFF)
 * -----------------------------------------------------------------------------
 * Capa intermedia que agrega y adapta datos de todos los microservicios
 * (inventario, pedidos, envios, proveedores, calificaciones, cupones,
 * notificaciones, pagos, sucursales, usuarios) en respuestas optimizadas
 * para el frontend React. El frontend nunca llama directamente a los
 * microservicios; siempre pasa por el BFF.
 */
@Service
public class BffService {

    private final RestTemplate restTemplate;

    @Value("${inventario.url:http://localhost:8081}")
    private String inventarioUrl;

    @Value("${pedidos.url:http://localhost:8082}")
    private String pedidosUrl;

    @Value("${envios.url:http://localhost:8083}")
    private String enviosUrl;

    @Value("${calificaciones.url:http://localhost:8088}")
    private String calificacionesUrl;

    @Value("${cupones.url:http://localhost:8091}")
    private String cuponesUrl;

    @Value("${notificaciones.url:http://localhost:8090}")
    private String notificacionesUrl;

    @Value("${pagos.url:http://localhost:8086}")
    private String pagosUrl;

    @Value("${proveedores.url:http://localhost:8089}")
    private String proveedoresUrl;

    @Value("${sucursales.url:http://localhost:8087}")
    private String sucursalesUrl;

    @Value("${usuarios.url:http://localhost:8092}")
    private String usuariosUrl;

    public BffService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Agrega datos de inventario, pedidos y envios en un solo DTO de dashboard.
     * Si algun servicio falla, retorna lista vacia para ese modulo.
     */
    public DashboardDTO obtenerDashboard() {
        List<Map<String, Object>> productos = fetchList(inventarioUrl + "/api/inventario");
        List<Map<String, Object>> pedidos   = fetchList(pedidosUrl   + "/api/pedidos");
        List<Map<String, Object>> envios    = fetchList(enviosUrl     + "/api/envios");

        long aprobados = pedidos.stream()
                .filter(p -> "APROBADO".equals(p.get("estado"))).count();
        long pendientes = pedidos.stream()
                .filter(p -> "CREADO".equals(p.get("estado")) || "VALIDADO".equals(p.get("estado"))).count();
        long envPendientes = envios.stream()
                .filter(e -> "PENDIENTE".equals(e.get("estado"))).count();

        DashboardDTO.ResumenDTO resumen = new DashboardDTO.ResumenDTO(
                productos.size(),
                pedidos.size(),
                (int) aprobados,
                (int) pendientes,
                envios.size(),
                (int) envPendientes
        );

        return new DashboardDTO(resumen, productos, pedidos, envios);
    }

    // ── Inventario ──────────────────────────────────────────────────────

    public List<Map<String, Object>> obtenerInventario() {
        return fetchList(inventarioUrl + "/api/inventario");
    }

    public Map<String, Object> crearProducto(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, inventarioUrl + "/api/inventario", body,
                "Error al crear producto");
    }

    public Map<String, Object> actualizarProducto(Long id, Map<String, Object> body) {
        return proxyMap(HttpMethod.PUT, inventarioUrl + "/api/inventario/" + id, body,
                "Error al actualizar producto");
    }

    public Map<String, Object> eliminarProducto(Long id) {
        try {
            restTemplate.delete(inventarioUrl + "/api/inventario/" + id);
            return Map.of("message", "Producto eliminado correctamente", "id", id);
        } catch (RestClientException e) {
            throw new RuntimeException("Error al eliminar producto: " + e.getMessage());
        }
    }

    // ── Pedidos ─────────────────────────────────────────────────────────

    public List<Map<String, Object>> obtenerPedidos() {
        return fetchList(pedidosUrl + "/api/pedidos");
    }

    public Map<String, Object> crearPedido(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, pedidosUrl + "/api/pedidos", body,
                "Error al crear pedido");
    }

    public Map<String, Object> cambiarEstadoPedido(Long id, Map<String, Object> body) {
        return proxyMap(HttpMethod.PATCH, pedidosUrl + "/api/pedidos/" + id + "/estado", body,
                "Error al cambiar estado del pedido");
    }

    // ── Envios ─────────────────────────────────────────────────────────

    public List<Map<String, Object>> obtenerEnvios() {
        return fetchList(enviosUrl + "/api/envios");
    }

    public Map<String, Object> crearEnvio(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, enviosUrl + "/api/envios", body,
                "Error al crear envio");
    }

    public Map<String, Object> actualizarEstadoEnvio(Long id, Map<String, Object> body) {
        return proxyMap(HttpMethod.PATCH, enviosUrl + "/api/envios/" + id + "/estado", body,
                "Error al actualizar estado del envio");
    }

    // ── Calificaciones ─────────────────────────────────────────────────

    public List<Map<String, Object>> obtenerCalificaciones() {
        return fetchList(calificacionesUrl + "/api/calificaciones");
    }

    public List<Map<String, Object>> obtenerCalificacionesPorProducto(Long productoId) {
        return fetchList(calificacionesUrl + "/api/calificaciones/producto/" + productoId);
    }

    public Map<String, Object> obtenerPromedioCalificacion(Long productoId) {
        return proxyMap(HttpMethod.GET, calificacionesUrl + "/api/calificaciones/producto/" + productoId + "/promedio",
                null, "Error al obtener promedio de calificaciones");
    }

    public Map<String, Object> crearCalificacion(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, calificacionesUrl + "/api/calificaciones", body,
                "Error al crear calificacion");
    }

    // ── Cupones (el microservicio expone /cupones, sin prefijo /api) ─────

    public List<Map<String, Object>> obtenerCupones() {
        return fetchList(cuponesUrl + "/cupones");
    }

    public Map<String, Object> obtenerCuponPorId(Long id) {
        return proxyMap(HttpMethod.GET, cuponesUrl + "/cupones/" + id, null,
                "Error al obtener cupon");
    }

    public Map<String, Object> crearCupon(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, cuponesUrl + "/cupones", body,
                "Error al crear cupon");
    }

    public Map<String, Object> desactivarCupon(Long id) {
        return proxyMap(HttpMethod.PATCH, cuponesUrl + "/cupones/" + id + "/desactivar", null,
                "Error al desactivar cupon");
    }

    public Map<String, Object> validarCupon(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, cuponesUrl + "/cupones/validar", body,
                "Error al validar cupon");
    }

    // ── Notificaciones (el microservicio expone /notificaciones, sin /api) ───

    public List<Map<String, Object>> obtenerNotificaciones() {
        return fetchList(notificacionesUrl + "/notificaciones");
    }

    public Map<String, Object> obtenerNotificacionPorId(Long id) {
        return proxyMap(HttpMethod.GET, notificacionesUrl + "/notificaciones/" + id, null,
                "Error al obtener notificacion");
    }

    public List<Map<String, Object>> obtenerNotificacionesPorDestinatario(String destinatario) {
        return fetchList(notificacionesUrl + "/notificaciones/destinatario/" + destinatario);
    }

    public List<Map<String, Object>> obtenerNotificacionesPorEstado(String estado) {
        return fetchList(notificacionesUrl + "/notificaciones/estado/" + estado);
    }

    public Map<String, Object> crearNotificacion(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, notificacionesUrl + "/notificaciones", body,
                "Error al crear notificacion");
    }

    public Map<String, Object> reenviarNotificacion(Long id) {
        return proxyMap(HttpMethod.POST, notificacionesUrl + "/notificaciones/" + id + "/reenviar", null,
                "Error al reenviar notificacion");
    }

    // ── Pagos ─────────────────────────────────────────────────────────

    public List<Map<String, Object>> obtenerPagos() {
        return fetchList(pagosUrl + "/api/pagos");
    }

    public Map<String, Object> procesarPago(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, pagosUrl + "/api/pagos/procesar", body,
                "Error al procesar pago");
    }

    public List<Map<String, Object>> obtenerPagosPorPedido(Long pedidoId) {
        return fetchList(pagosUrl + "/api/pagos/pedido/" + pedidoId);
    }

    // ── Proveedores ─────────────────────────────────────────────────

    public List<Map<String, Object>> obtenerProveedores() {
        return fetchList(proveedoresUrl + "/api/proveedores");
    }

    public List<Map<String, Object>> obtenerProveedoresActivos() {
        return fetchList(proveedoresUrl + "/api/proveedores/activos");
    }

    public Map<String, Object> crearProveedor(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, proveedoresUrl + "/api/proveedores", body,
                "Error al crear proveedor");
    }

    public Map<String, Object> actualizarProveedor(Long id, Map<String, Object> body) {
        return proxyMap(HttpMethod.PUT, proveedoresUrl + "/api/proveedores/" + id, body,
                "Error al actualizar proveedor");
    }

    public Map<String, Object> desactivarProveedor(Long id) {
        return proxyMap(HttpMethod.PATCH, proveedoresUrl + "/api/proveedores/" + id + "/desactivar", null,
                "Error al desactivar proveedor");
    }

    // ── Sucursales ─────────────────────────────────────────────────

    public List<Map<String, Object>> obtenerSucursales() {
        return fetchList(sucursalesUrl + "/api/sucursales");
    }

    public List<Map<String, Object>> obtenerSucursalesActivas() {
        return fetchList(sucursalesUrl + "/api/sucursales/activas");
    }

    public Map<String, Object> crearSucursal(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, sucursalesUrl + "/api/sucursales", body,
                "Error al crear sucursal");
    }

    public Map<String, Object> actualizarSucursal(Long id, Map<String, Object> body) {
        return proxyMap(HttpMethod.PUT, sucursalesUrl + "/api/sucursales/" + id, body,
                "Error al actualizar sucursal");
    }

    public Map<String, Object> cambiarEstadoSucursal(Long id, Map<String, Object> body) {
        return proxyMap(HttpMethod.PATCH, sucursalesUrl + "/api/sucursales/" + id + "/estado", body,
                "Error al cambiar estado de sucursal");
    }

    // ── Usuarios ───────────────────────────────────────────────────

    public List<Map<String, Object>> obtenerUsuarios() {
        return fetchList(usuariosUrl + "/api/usuarios");
    }

    // ── Helpers ─────────────────────────────────────────────────────────

    private List<Map<String, Object>> fetchList(String url) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException e) {
            System.err.println("[BFF] Servicio no disponible en: " + url + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> proxyMap(HttpMethod method, String url, Map<String, Object> body, String errorMessage) {
        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body);
            ResponseEntity<Map> response = restTemplate.exchange(url, method, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            return responseBody != null ? responseBody : Collections.emptyMap();
        } catch (RestClientException e) {
            throw new RuntimeException(errorMessage + ": " + e.getMessage());
        }
    }
}
