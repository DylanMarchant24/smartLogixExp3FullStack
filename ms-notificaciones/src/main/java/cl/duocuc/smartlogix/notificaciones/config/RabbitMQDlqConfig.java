package cl.duocuc.smartlogix.notificaciones.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Infraestructura de mensajes no entregados (Dead Letter) para ms-notificaciones.
 * Todos los nombres de exchanges, colas y routing keys se inyectan desde application.yml.
 */
@Configuration
public class RabbitMQDlqConfig {

    public static final String DEFAULT_DLX_NAME = "smartlogix.dlx";
    public static final String DEFAULT_DLQ_NOTIFICACIONES = "smartlogix.notificaciones.dlq";
    public static final String DEFAULT_DLQ_ROUTING_KEY = "notificaciones.dead";

    @Value("${mensajeria.exchange.dlx:" + DEFAULT_DLX_NAME + "}")
    private String dlxName = DEFAULT_DLX_NAME;

    @Value("${mensajeria.colas.dlq-notificaciones:" + DEFAULT_DLQ_NOTIFICACIONES + "}")
    private String dlqNotificaciones = DEFAULT_DLQ_NOTIFICACIONES;

    @Value("${mensajeria.routing-keys.dlq-notificaciones:" + DEFAULT_DLQ_ROUTING_KEY + "}")
    private String dlqRoutingKey = DEFAULT_DLQ_ROUTING_KEY;

    @Bean
    public DirectExchange smartlogixDeadLetterExchange() {
        return new DirectExchange(dlxName, true, false);
    }

    @Bean
    public Queue notificacionesDeadLetterQueue() {
        return QueueBuilder.durable(dlqNotificaciones).build();
    }

    @Bean
    public Binding bindingNotificacionesDlq(Queue notificacionesDeadLetterQueue,
                                            DirectExchange smartlogixDeadLetterExchange) {
        return BindingBuilder.bind(notificacionesDeadLetterQueue)
                .to(smartlogixDeadLetterExchange)
                .with(dlqRoutingKey);
    }
}
