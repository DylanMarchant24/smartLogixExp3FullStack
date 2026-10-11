package cl.duocuc.smartlogix.pedidos.service;

import cl.duocuc.smartlogix.pedidos.dto.PedidoDTO;
import cl.duocuc.smartlogix.pedidos.event.PedidoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Publicador de eventos AMQP para pedidos.
 * Envía mensajes al TopicExchange usando nombres centralizados desde properties.
 */
@Slf4j
@Service
public class PedidoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    // 1. Inyectamos las variables dinámicas del application.properties
    @Value("${mensajeria.exchange.pedidos:smartlogix.exchange}")
    private String exchangeName;

    @Value("${mensajeria.routing-keys.pedido-creado:pedido.creado}")
    private String routingKeyPedidoCreado;

    @Value("${mensajeria.routing-keys.pedido-actualizado:pedido.actualizado}")
    private String routingKeyPedidoActualizado;

    public PedidoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarPedidoCreado(PedidoDTO pedido) {
        PedidoEvent event = PedidoEvent.builder()
                .pedidoId(pedido.getId())
                .skuProducto(pedido.getSkuProducto())
                .cantidad(pedido.getCantidad())
                .clienteEmail(pedido.getClienteEmail())
                .estado(pedido.getEstado())
                .tipoEvento("PEDIDO_CREADO")
                .routingKey(routingKeyPedidoCreado)
                .fechaHora(LocalDateTime.now().toString())
                .mensaje("Nuevo pedido #" + pedido.getId() + " registrado con estado " + pedido.getEstado())
                .build();

        try {
            log.info("[RabbitMQ] Publicando evento '{}' a exchange '{}' con routing key '{}'",
                    event.getTipoEvento(), exchangeName, routingKeyPedidoCreado);
            // 2. Usamos las variables inyectadas en lugar de textos quemados
            rabbitTemplate.convertAndSend(
                    exchangeName,
                    routingKeyPedidoCreado,
                    event
            );
        } catch (Exception e) {
            log.error("[RabbitMQ] Error al publicar evento de pedido creado: {}", e.getMessage(), e);
        }
    }

    public void publicarPedidoActualizado(PedidoDTO pedido) {
        PedidoEvent event = PedidoEvent.builder()
                .pedidoId(pedido.getId())
                .skuProducto(pedido.getSkuProducto())
                .cantidad(pedido.getCantidad())
                .clienteEmail(pedido.getClienteEmail())
                .estado(pedido.getEstado())
                .tipoEvento("PEDIDO_ACTUALIZADO")
                .routingKey(routingKeyPedidoActualizado)
                .fechaHora(LocalDateTime.now().toString())
                .mensaje("Pedido #" + pedido.getId() + " actualizado a estado " + pedido.getEstado())
                .build();

        try {
            log.info("[RabbitMQ] Publicando evento '{}' a exchange '{}' con routing key '{}'",
                    event.getTipoEvento(), exchangeName, routingKeyPedidoActualizado);
            // 3. Usamos las variables inyectadas
            rabbitTemplate.convertAndSend(
                    exchangeName,
                    routingKeyPedidoActualizado,
                    event
            );
        } catch (Exception e) {
            log.error("[RabbitMQ] Error al publicar evento de pedido actualizado: {}", e.getMessage(), e);
        }
    }
}