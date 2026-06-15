package cl.duocuc.smartlogix.pedidos.factory;

import cl.duocuc.smartlogix.pedidos.model.EstadoPedido;
import cl.duocuc.smartlogix.pedidos.model.Pedido;

import java.time.LocalDateTime;

/**
 * PATRÓN: Factory Method
 * ─────────────────────────────────────────────────────────────────────────
 * Centraliza la creación de objetos Pedido según el estado del ciclo de vida.
 * Beneficio: si la lógica de inicialización cambia (ej. nuevos campos),
 * sólo se modifica aquí y no en múltiples lugares del servicio.
 *
 * Tres "factories" representando las 3 fases clave del flujo:
 *   crearNuevo    → CREADO   (sin validación de stock – circuit abierto)
 *   crearValidado → VALIDADO (stock confirmado por ms-inventario)
 *   crearAprobado → APROBADO (stock descontado, listo para despacho)
 */
public class PedidoFactory {

    private PedidoFactory() {
        // Clase utilitaria – no instanciable
    }

    /**
     * Estado CREADO: el pedido se registra pero aún no se validó stock.
     * Se usa como fallback cuando el Circuit Breaker está abierto.
     */
    public static Pedido crearNuevo(String sku, int cantidad, String clienteEmail) {
        Pedido pedido = new Pedido();
        pedido.setSkuProducto(sku);
        pedido.setCantidad(cantidad);
        pedido.setClienteEmail(clienteEmail);
        pedido.setEstado(EstadoPedido.CREADO);
        pedido.setFechaCreacion(LocalDateTime.now());
        return pedido;
    }

    /**
     * Estado VALIDADO: ms-inventario confirmó stock suficiente.
     * Se guarda el stock actual como referencia (fallback futuro).
     */
    public static Pedido crearValidado(String sku, int cantidad, String clienteEmail, int stockActual) {
        Pedido pedido = crearNuevo(sku, cantidad, clienteEmail);
        pedido.setEstado(EstadoPedido.VALIDADO);
        pedido.setUltimoStockConocido(stockActual);
        return pedido;
    }

    /**
     * Estado APROBADO: el stock fue descontado exitosamente en ms-inventario.
     * El pedido queda listo para ser traspasado al módulo de envíos.
     */
    public static Pedido crearAprobado(String sku, int cantidad, String clienteEmail, int stockActual) {
        Pedido pedido = crearValidado(sku, cantidad, clienteEmail, stockActual);
        pedido.setEstado(EstadoPedido.APROBADO);
        return pedido;
    }
}
