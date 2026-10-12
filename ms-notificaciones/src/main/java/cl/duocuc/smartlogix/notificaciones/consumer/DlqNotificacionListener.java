package cl.duocuc.smartlogix.notificaciones.consumer;

import cl.duocuc.smartlogix.notificaciones.config.RabbitMQDlqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Registra en logs los mensajes muertos de 'smartlogix.notificaciones.dlq'.
 *
 * IMPORTANTE: este listener CONSUME (y confirma) los mensajes de la DLQ, por lo que la cola
 * queda vacia. Por eso esta desactivado por defecto; se activa con:
 *   smartlogix.dlq.logger.enabled=true
 * Mientras este desactivado, los mensajes permanecen en la DLQ para inspeccionarlos en el dashboard.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "smartlogix.dlq.logger.enabled", havingValue = "true")
public class DlqNotificacionListener {

    @RabbitListener(queues = RabbitMQDlqConfig.DLQ_NOTIFICACIONES)
    public void registrarMensajeMuerto(Message message) {
        Object razon = message.getMessageProperties().getHeaders().get("x-first-death-reason");
        Object colaOrigen = message.getMessageProperties().getHeaders().get("x-first-death-queue");
        String cuerpo = new String(message.getBody(), StandardCharsets.UTF_8);

        log.warn("[RabbitMQ DLQ - ms-notificaciones] Mensaje muerto recibido en '{}'. colaOrigen={}, razon={}, xDeath={}, cuerpo={}",
                RabbitMQDlqConfig.DLQ_NOTIFICACIONES, colaOrigen, razon,
                message.getMessageProperties().getXDeathHeader(), cuerpo);
    }
}
