package cl.duocuc.smartlogix.notificaciones.consumer;

import cl.duocuc.smartlogix.notificaciones.event.PedidoEvent;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor de auditoría para la Dead Letter Queue (DLQ) de ms-notificaciones.
 * Escucha la cola definida en application.yml y mapea directamente el DTO PedidoEvent
 * con confirmación manual (basicAck).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "smartlogix.dlq.logger.enabled", havingValue = "true")
public class DlqNotificacionListener {

    @Value("${mensajeria.colas.dlq-notificaciones}")
    private String dlqNotificaciones;

    @RabbitListener(queues = "${mensajeria.colas.dlq-notificaciones}")
    public void registrarMensajeMuerto(
            @Payload PedidoEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            @Header(name = "x-first-death-reason", required = false) String razon,
            @Header(name = "x-first-death-queue", required = false) String colaOrigen) throws IOException {

        try {
            log.warn("[RabbitMQ DLQ - ms-notificaciones] Evento muerto recibido en '{}'. colaOrigen={}, razon={}, evento={}",
                    dlqNotificaciones, colaOrigen, razon, event);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[RabbitMQ DLQ - ms-notificaciones] Error al auditar mensaje en DLQ: {}", e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
