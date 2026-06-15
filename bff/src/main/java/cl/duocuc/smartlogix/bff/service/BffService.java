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
 * Capa intermedia que agrega y adapta datos de los tres microservicios
 * (inventario, pedidos, envios) en respuestas optimizadas para el frontend React.
 *
 * Beneficio: el frontend realiza una unica integracion con el BFF, reduciendo
 * acoplamiento, duplicidad de logica y latencia percibida en el cliente.
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

    /** Proxy: reenvia solicitudes de inventario desde el frontend. */
    public List<Map<String, Object>> obtenerInventario() {
        return fetchList(inventarioUrl + "/api/inventario");
    }

    /** Proxy: crea un producto en ms-inventario. */
    public Map<String, Object> crearProducto(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, inventarioUrl + "/api/inventario", body,
                "Error al crear producto");
    }

    /** Proxy: actualiza un producto en ms-inventario. */
    public Map<String, Object> actualizarProducto(Long id, Map<String, Object> body) {
        return proxyMap(HttpMethod.PUT, inventarioUrl + "/api/inventario/" + id, body,
                "Error al actualizar producto");
    }

    /** Proxy: elimina un producto en ms-inventario. */
    public Map<String, Object> eliminarProducto(Long id) {
        try {
            restTemplate.delete(inventarioUrl + "/api/inventario/" + id);
            return Map.of("message", "Producto eliminado correctamente", "id", id);
        } catch (RestClientException e) {
            throw new RuntimeException("Error al eliminar producto: " + e.getMessage());
        }
    }

    /** Proxy: reenvia solicitudes de pedidos desde el frontend. */
    public List<Map<String, Object>> obtenerPedidos() {
        return fetchList(pedidosUrl + "/api/pedidos");
    }

    /** Proxy: crea un pedido en ms-pedidos. */
    public Map<String, Object> crearPedido(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, pedidosUrl + "/api/pedidos", body,
                "Error al crear pedido");
    }

    /** Proxy: cambia estado de un pedido en ms-pedidos. */
    public Map<String, Object> cambiarEstadoPedido(Long id, Map<String, Object> body) {
        return proxyMap(HttpMethod.PATCH, pedidosUrl + "/api/pedidos/" + id + "/estado", body,
                "Error al cambiar estado del pedido");
    }

    /** Proxy: reenvia solicitudes de envios desde el frontend. */
    public List<Map<String, Object>> obtenerEnvios() {
        return fetchList(enviosUrl + "/api/envios");
    }

    /** Proxy: crea un envio en ms-envios. */
    public Map<String, Object> crearEnvio(Map<String, Object> body) {
        return proxyMap(HttpMethod.POST, enviosUrl + "/api/envios", body,
                "Error al crear envio");
    }

    /** Proxy: actualiza estado de un envio en ms-envios. */
    public Map<String, Object> actualizarEstadoEnvio(Long id, Map<String, Object> body) {
        return proxyMap(HttpMethod.PATCH, enviosUrl + "/api/envios/" + id + "/estado", body,
                "Error al actualizar estado del envio");
    }

    // Helpers

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
