package cl.duocuc.smartlogix.bff;

import cl.duocuc.smartlogix.bff.dto.DashboardDTO;
import cl.duocuc.smartlogix.bff.service.BffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("BffService - Pruebas Unitarias")
class BffServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BffService bffService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        injectField(bffService, "inventarioUrl", "http://localhost:8081");
        injectField(bffService, "pedidosUrl",    "http://localhost:8082");
        injectField(bffService, "enviosUrl",     "http://localhost:8083");
    }

    @Test
    @DisplayName("obtenerDashboard: combina datos de los tres servicios")
    void obtenerDashboard_combinaTresServicios() {
        Map<String, Object> producto = Map.of("id", 1, "nombre", "Laptop", "stock", 10);
        Map<String, Object> pedido = Map.of("id", 1, "estado", "APROBADO");
        Map<String, Object> envio = Map.of("id", 1, "estado", "PENDIENTE");

        mockDashboardExchange(List.of(producto), List.of(pedido), List.of(envio));

        DashboardDTO result = bffService.obtenerDashboard();

        assertNotNull(result);
        assertEquals(1, result.getResumen().getTotalProductos());
        assertEquals(1, result.getResumen().getTotalPedidos());
        assertEquals(1, result.getResumen().getPedidosAprobados());
        assertEquals(1, result.getResumen().getEnviosPendientes());
    }

    @Test
    @DisplayName("obtenerDashboard: retorna vacio si un servicio falla")
    void obtenerDashboard_servicioFalla_retornaVacio() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()))
                .thenThrow(new RestClientException("Servicio no disponible"));

        DashboardDTO result = bffService.obtenerDashboard();

        assertNotNull(result);
        assertEquals(0, result.getResumen().getTotalProductos());
        assertTrue(result.getProductos().isEmpty());
    }

    @Test
    @DisplayName("crearPedido: delega al ms-pedidos y retorna respuesta")
    void crearPedido_delegaCorrectamente() {
        Map<String, Object> body = Map.of("skuProducto", "SKU-001", "cantidad", 2);
        Map<String, Object> respMock = Map.of("id", 5, "estado", "APROBADO");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(Map.class)))
                .thenReturn(rawMapResponse(respMock));

        Map<String, Object> result = bffService.crearPedido(body);
        assertEquals("APROBADO", result.get("estado"));
    }

    @Test
    @DisplayName("crearProducto: delega al ms-inventario")
    void crearProducto_delegaCorrectamente() {
        Map<String, Object> body = Map.of("nombre", "Teclado", "sku", "SKU-100", "stock", 15);
        Map<String, Object> respMock = Map.of("id", 10, "nombre", "Teclado");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(Map.class)))
                .thenReturn(rawMapResponse(respMock));

        Map<String, Object> result = bffService.crearProducto(body);
        assertEquals("Teclado", result.get("nombre"));
    }

    @Test
    @DisplayName("actualizarProducto: delega al ms-inventario mediante PUT")
    void actualizarProducto_delegaCorrectamente() {
        Map<String, Object> body = Map.of("nombre", "Mouse", "stock", 20);
        Map<String, Object> respMock = Map.of("id", 11, "nombre", "Mouse");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(Map.class)))
                .thenReturn(rawMapResponse(respMock));

        Map<String, Object> result = bffService.actualizarProducto(11L, body);
        assertEquals(11, result.get("id"));
    }

    @Test
    @DisplayName("cambiarEstadoPedido: delega al ms-pedidos mediante PATCH")
    void cambiarEstadoPedido_delegaCorrectamente() {
        Map<String, Object> body = Map.of("estado", "VALIDADO");
        Map<String, Object> respMock = Map.of("id", 7, "estado", "VALIDADO");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(Map.class)))
                .thenReturn(rawMapResponse(respMock));

        Map<String, Object> result = bffService.cambiarEstadoPedido(7L, body);
        assertEquals("VALIDADO", result.get("estado"));
    }

    @Test
    @DisplayName("actualizarEstadoEnvio: delega al ms-envios mediante PATCH")
    void actualizarEstadoEnvio_delegaCorrectamente() {
        Map<String, Object> body = Map.of("estado", "EN_TRANSITO");
        Map<String, Object> respMock = Map.of("id", 8, "estado", "EN_TRANSITO");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(Map.class)))
                .thenReturn(rawMapResponse(respMock));

        Map<String, Object> result = bffService.actualizarEstadoEnvio(8L, body);
        assertEquals("EN_TRANSITO", result.get("estado"));
    }

    @Test
    @DisplayName("eliminarProducto: delega al ms-inventario mediante DELETE")
    void eliminarProducto_delegaCorrectamente() {
        doNothing().when(restTemplate).delete(anyString());

        Map<String, Object> result = bffService.eliminarProducto(12L);

        assertEquals("Producto eliminado correctamente", result.get("message"));
        verify(restTemplate).delete("http://localhost:8081/api/inventario/12");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ResponseEntity<Map> rawMapResponse(Map<String, Object> body) {
        return ResponseEntity.ok((Map) body);
    }

    private void mockDashboardExchange(
            List<Map<String, Object>> productos,
            List<Map<String, Object>> pedidos,
            List<Map<String, Object>> envios) {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()))
                .thenReturn(ResponseEntity.ok(productos))
                .thenReturn(ResponseEntity.ok(pedidos))
                .thenReturn(ResponseEntity.ok(envios))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));
    }

    private void injectField(Object target, String fieldName, String value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
