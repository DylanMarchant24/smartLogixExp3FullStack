package cl.duocuc.smartlogix.pedidos.service;

import cl.duocuc.smartlogix.pedidos.dto.PedidoDTO;
import cl.duocuc.smartlogix.pedidos.event.PedidoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Publicador de eventos para pedidos.
 * Envía mensajes asíncronos tanto a RabbitMQ como a Kafka usando configuración centralizada.
 */
@Slf4j
@Service
public class PedidoEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    // 1. Inyectamos el template de Kafka tipado con tu DTO de evento
    private final KafkaTemplate<String, PedidoEvent> kafkaTemplate;

    @Value("${mensajeria.exchange.pedidos:smartlogix.exchange}")
    private String exchangeName;

    @Value("${mensajeria.routing-keys.pedido-creado:pedido.creado}")
    private String routingKeyPedidoCreado;

    @Value("${mensajeria.routing-keys.pedido-actualizado:pedido.actualizado}")
    private String routingKeyPedidoActualizado;

    // 2. Definimos el tópico de Kafka por defecto
    @Value("${kafka.topic.pedidos:pedidos_topic}")
    private String kafkaTopic;

    public PedidoEventPublisher(RabbitTemplate rabbitTemplate, KafkaTemplate<String, PedidoEvent> kafkaTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.kafkaTemplate = kafkaTemplate;
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
            rabbitTemplate.convertAndSend(exchangeName, routingKeyPedidoCreado, event);

            // 3. Publicación en Kafka usando el ID del pedido como llave (Key)
            log.info("[Kafka] Publicando evento '{}' al tópico '{}'", event.getTipoEvento(), kafkaTopic);
            kafkaTemplate.send(kafkaTopic, String.valueOf(pedido.getId()), event);

        } catch (Exception e) {
            log.error("[Mensajería] Error al publicar evento de pedido creado: {}", e.getMessage(), e);
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
            rabbitTemplate.convertAndSend(exchangeName, routingKeyPedidoActualizado, event);

            // 4. Publicación en Kafka para actualizaciones
            log.info("[Kafka] Publicando evento '{}' al tópico '{}'", event.getTipoEvento(), kafkaTopic);
            kafkaTemplate.send(kafkaTopic, String.valueOf(pedido.getId()), event);

        } catch (Exception e) {
            log.error("[Mensajería] Error al publicar evento de pedido actualizado: {}", e.getMessage(), e);
        }
    }
}