package cl.duocuc.smartlogix.notificaciones.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RabbitMQDlqConfigTest {

    @Test
    void colaPrincipal_tieneDlxYRoutingKeyConfigurados() {
        Queue queue = new RabbitMQConfig().notificacionesQueue();

        assertEquals(RabbitMQConfig.QUEUE_NOTIFICACIONES, queue.getName());
        assertTrue(queue.isDurable());
        assertEquals(RabbitMQDlqConfig.DLX_NAME, queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals(RabbitMQDlqConfig.DLQ_ROUTING_KEY, queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void dlq_estaEnlazadaAlDlxConSuRoutingKey() {
        RabbitMQDlqConfig config = new RabbitMQDlqConfig();
        DirectExchange dlx = config.smartlogixDeadLetterExchange();
        Queue dlq = config.notificacionesDeadLetterQueue();
        Binding binding = config.bindingNotificacionesDlq(dlq, dlx);

        assertEquals(RabbitMQDlqConfig.DLQ_NOTIFICACIONES, dlq.getName());
        assertTrue(dlq.isDurable());
        assertEquals(RabbitMQDlqConfig.DLX_NAME, binding.getExchange());
        assertEquals(RabbitMQDlqConfig.DLQ_ROUTING_KEY, binding.getRoutingKey());
        assertEquals(RabbitMQDlqConfig.DLQ_NOTIFICACIONES, binding.getDestination());
    }
}
