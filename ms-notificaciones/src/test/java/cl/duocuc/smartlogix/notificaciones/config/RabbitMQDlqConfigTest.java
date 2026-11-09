package cl.duocuc.smartlogix.notificaciones.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class RabbitMQDlqConfigTest {

    @Test
    void colaPrincipal_tieneDlxYRoutingKeyConfigurados() {
        RabbitMQConfig config = new RabbitMQConfig();
        Queue queue = config.notificacionesQueue();

        assertEquals(RabbitMQConfig.DEFAULT_QUEUE_NOTIFICACIONES, queue.getName());
        assertTrue(queue.isDurable());
        assertEquals(RabbitMQDlqConfig.DEFAULT_DLX_NAME, queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals(RabbitMQDlqConfig.DEFAULT_DLQ_ROUTING_KEY, queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void dlq_estaEnlazadaAlDlxConSuRoutingKey() {
        RabbitMQDlqConfig config = new RabbitMQDlqConfig();
        DirectExchange dlx = config.smartlogixDeadLetterExchange();
        Queue dlq = config.notificacionesDeadLetterQueue();
        Binding binding = config.bindingNotificacionesDlq(dlq, dlx);

        assertEquals(RabbitMQDlqConfig.DEFAULT_DLQ_NOTIFICACIONES, dlq.getName());
        assertTrue(dlq.isDurable());
        assertEquals(RabbitMQDlqConfig.DEFAULT_DLX_NAME, binding.getExchange());
        assertEquals(RabbitMQDlqConfig.DEFAULT_DLQ_ROUTING_KEY, binding.getRoutingKey());
        assertEquals(RabbitMQDlqConfig.DEFAULT_DLQ_NOTIFICACIONES, binding.getDestination());
    }

    @Test
    void listenerContainerFactory_configuraAcknowledgeModeManual() {
        RabbitMQConfig config = new RabbitMQConfig();
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        SimpleRabbitListenerContainerFactory factory = config.rabbitListenerContainerFactory(
                connectionFactory,
                config.jsonMessageConverter()
        );

        assertNotNull(factory);
        var container = factory.createListenerContainer();
        assertEquals(AcknowledgeMode.MANUAL, container.getAcknowledgeMode());
    }
}
