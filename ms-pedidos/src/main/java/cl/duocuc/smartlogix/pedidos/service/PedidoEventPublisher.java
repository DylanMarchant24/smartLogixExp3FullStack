package cl.duocuc.smartlogix.pedidos.service;

import cl.duocuc.smartlogix.pedidos.dto.PedidoDTO;
import cl.duocuc.smartlogix.pedidos.event.PedidoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Publicador de eventos para pedidos (RabbitMQ + Apache Kafka).
 * - Publica en RabbitMQ (TopicExchange 'smartlogix.exchange') para notificaciones y envíos.
 * - Publica en Apache Kafka (tópico 'smartlogix.pedidos.eventos') con DTO tipado y clave de partición.
 */
@Slf4j
@Service
public class PedidoEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${mensajeria.exchange.pedidos:smartlogix.exchange}")
    private String exchangeName = "smartlogix.exchange";

    @Value("${mensajeria.routing-keys.pedido-creado:pedido.creado}")
    private String routingKeyPedidoCreado = "pedido.creado";

    @Value("${mensajeria.routing-keys.pedido-actualizado:pedido.actualizado}")
    private String routingKeyPedidoActualizado = "pedido.actualizado";

    @Value("${mensajeria.kafka.topics.pedidos-eventos:smartlogix.pedidos.eventos}")
    private String kafkaTopicPedidos = "smartlogix.pedidos.eventos";

    public PedidoEventPublisher(RabbitTemplate rabbitTemplate) {
        this(rabbitTemplate, null);
    }

    @Autowired
    public PedidoEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Autowired(required = false) KafkaTemplate<String, Object> kafkaTemplate) {
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
        } catch (Exception e) {
            log.error("[RabbitMQ] Error al publicar evento de pedido creado: {}", e.getMessage(), e);
        }

        publicarEnKafka(event);
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
        } catch (Exception e) {
            log.error("[RabbitMQ] Error al publicar evento de pedido actualizado: {}", e.getMessage(), e);
        }

        publicarEnKafka(event);
    }

    private void publicarEnKafka(PedidoEvent event) {
        if (kafkaTemplate == null) {
            return;
        }
        try {
            String key = event.getPedidoId() != null ? String.valueOf(event.getPedidoId()) : "0";
            log.info("[Kafka Producer - ms-pedidos] Publicando evento '{}' en tópico '{}' con key='{}'",
                    event.getTipoEvento(), kafkaTopicPedidos, key);
            kafkaTemplate.send(kafkaTopicPedidos, key, event);
        } catch (Exception e) {
            log.error("[Kafka Producer - ms-pedidos] Error al publicar evento en Kafka: {}", e.getMessage(), e);
        }
    }
}
