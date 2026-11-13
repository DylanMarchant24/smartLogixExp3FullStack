package cl.duocuc.smartlogix.auditoria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO tipado para deserializar eventos del tópico 'smartlogix.pedidos.eventos'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoKafkaEventDTO implements Serializable {

    private Long pedidoId;
    private String skuProducto;
    private Integer cantidad;
    private String clienteEmail;
    private String estado;
    private String tipoEvento;
    private String fechaHora;
    private String routingKey;
    private String mensaje;
}

