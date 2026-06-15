package cl.duocuc.smartlogix.bff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO de respuesta del BFF para el Dashboard.
 * Agrega datos de inventario y pedidos en una sola respuesta,
 * evitando múltiples llamadas desde el frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private ResumenDTO resumen;
    private List<Map<String, Object>> productos;
    private List<Map<String, Object>> pedidos;
    private List<Map<String, Object>> envios;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenDTO {
        private int totalProductos;
        private int totalPedidos;
        private int pedidosAprobados;
        private int pedidosPendientes;
        private int totalEnvios;
        private int enviosPendientes;
    }
}
