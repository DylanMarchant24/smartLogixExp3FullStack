package cl.duocuc.smartlogix.pedidos.service;

import cl.duocuc.smartlogix.pedidos.config.RabbitMQConfig;
import cl.duocuc.smartlogix.pedidos.dto.PedidoDTO;
import cl.duocuc.smartlogix.pedidos.event.PedidoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Publicador de eventos AMQP para pedidos.
 * Envía mensajes al TopicExchange 'smartlogix.exchange' usando Routing Keys específicas.
 */
@Slf4j
@Service
public class PedidoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public PedidoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publica evento al TopicExchange con routing key 'pedido.creado'.
     */
    public void publicarPedidoCreado(PedidoDTO pedido) {
        PedidoEvent event = PedidoEvent.builder()
                .pedidoId(pedido.getId())
                .skuProducto(pedido.getSkuProducto())
                .cantidad(pedido.getCantidad())
                .clienteEmail(pedido.getClienteEmail())
                .estado(pedido.getEstado())
                .tipoEvento("PEDIDO_CREADO")
                .routingKey(RabbitMQConfig.ROUTING_KEY_PEDIDO_CREADO)
                .fechaHora(LocalDateTime.now().toString())
                .mensaje("Nuevo pedido #" + pedido.getId() + " registrado con estado " + pedido.getEstado())
                .build();

        try {
            log.info("[RabbitMQ] Publicando evento '{}' a exchange '{}' con routing key '{}'",
                    event.getTipoEvento(), RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_PEDIDO_CREADO);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_PEDIDO_CREADO,
                    event
            );
        } catch (Exception e) {
            log.error("[RabbitMQ] Error al publicar evento de pedido creado: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica evento al TopicExchange con routing key 'pedido.actualizado'.
     */
    public void publicarPedidoActualizado(PedidoDTO pedido) {
        PedidoEvent event = PedidoEvent.builder()
                .pedidoId(pedido.getId())
                .skuProducto(pedido.getSkuProducto())
                .cantidad(pedido.getCantidad())
                .clienteEmail(pedido.getClienteEmail())
                .estado(pedido.getEstado())
                .tipoEvento("PEDIDO_ACTUALIZADO")
                .routingKey(RabbitMQConfig.ROUTING_KEY_PEDIDO_ACTUALIZADO)
                .fechaHora(LocalDateTime.now().toString())
                .mensaje("Pedido #" + pedido.getId() + " actualizado a estado " + pedido.getEstado())
                .build();

        try {
            log.info("[RabbitMQ] Publicando evento '{}' a exchange '{}' con routing key '{}'",
                    event.getTipoEvento(), RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_PEDIDO_ACTUALIZADO);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_PEDIDO_ACTUALIZADO,
                    event
            );
        } catch (Exception e) {
            log.error("[RabbitMQ] Error al publicar evento de pedido actualizado: {}", e.getMessage(), e);
        }
    }
}
