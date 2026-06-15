package cl.duocuc.smartlogix.pedidos.model;

/**
 * Estados del ciclo de vida de un pedido.
 * Usado por el PATRÓN Factory Method para determinar
 * qué objeto Pedido construir según el contexto.
 */
public enum EstadoPedido {
    CREADO,      // Registrado, pendiente de validación de stock
    VALIDADO,    // Stock confirmado por ms-inventario
    APROBADO,    // Stock descontado, listo para despacho
    RECHAZADO,   // Stock insuficiente o error de negocio
    DESPACHADO   // Traspasado al ms-envios
}
