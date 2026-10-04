package cl.duocuc.smartlogix.pedidos.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Evento de dominio emitido por ms-pedidos a través de RabbitMQ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long pedidoId;
    private String skuProducto;
    private Integer cantidad;
    private String clienteEmail;
    private String estado;
    private String tipoEvento; // "PEDIDO_CREADO" o "PEDIDO_ACTUALIZADO"
    private String fechaHora;
    private String routingKey;
    private String mensaje;
}
