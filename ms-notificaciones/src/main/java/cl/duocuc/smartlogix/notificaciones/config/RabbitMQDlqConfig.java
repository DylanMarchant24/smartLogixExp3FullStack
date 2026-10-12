package cl.duocuc.smartlogix.notificaciones.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Infraestructura de mensajes no entregados (Dead Letter) para ms-notificaciones:
 * - DLX: DirectExchange 'smartlogix.dlx'.
 * - DLQ: Cola durable 'smartlogix.notificaciones.dlq'.
 * - BINDING: DLQ enlazada al DLX con routing key 'notificaciones.dead'.
 * Los mensajes rechazados (requeue=false) de 'smartlogix.notificaciones.queue' terminan aqui.
 */
@Configuration
public class RabbitMQDlqConfig {

    public static final String DLX_NAME = "smartlogix.dlx";
    public static final String DLQ_NOTIFICACIONES = "smartlogix.notificaciones.dlq";
    public static final String DLQ_ROUTING_KEY = "notificaciones.dead";

    @Bean
    public DirectExchange smartlogixDeadLetterExchange() {
        return new DirectExchange(DLX_NAME, true, false);
    }

    @Bean
    public Queue notificacionesDeadLetterQueue() {
        return QueueBuilder.durable(DLQ_NOTIFICACIONES).build();
    }

    @Bean
    public Binding bindingNotificacionesDlq(Queue notificacionesDeadLetterQueue,
                                            DirectExchange smartlogixDeadLetterExchange) {
        return BindingBuilder.bind(notificacionesDeadLetterQueue)
                .to(smartlogixDeadLetterExchange)
                .with(DLQ_ROUTING_KEY);
    }
}
