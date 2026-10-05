package cl.duocuc.smartlogix.pedidos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de infraestructura RabbitMQ para ms-pedidos:
 * - EXCHANGE: TopicExchange ('smartlogix.exchange') para enrutamiento dinámico.
 * - QUEUES: Colas para notificaciones y envíos.
 * - BINDINGS: Conexiones entre Exchange y Colas usando Routing Keys ('pedido.creado', 'pedido.*').
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "smartlogix.exchange";
    public static final String QUEUE_NOTIFICACIONES = "smartlogix.notificaciones.queue";
    public static final String QUEUE_ENVIOS = "smartlogix.envios.queue";

    // Routing Keys
    public static final String ROUTING_KEY_PEDIDO_CREADO = "pedido.creado";
    public static final String ROUTING_KEY_PEDIDO_ACTUALIZADO = "pedido.actualizado";
    public static final String ROUTING_KEY_PEDIDO_PATTERN = "pedido.*";

    /**
     * 1. EXCHANGE: TopicExchange que distribuye los mensajes según la Routing Key.
     */
    @Bean
    public TopicExchange smartlogixExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    /**
     * Colas durables (sobreviven reinicios del broker RabbitMQ).
     */
    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(QUEUE_NOTIFICACIONES).build();
    }

    @Bean
    public Queue enviosQueue() {
        return QueueBuilder.durable(QUEUE_ENVIOS).build();
    }

    /**
     * 2. BINDINGS:
     * - La cola de notificaciones escucha cualquier evento 'pedido.*' (creado, actualizado).
     * - La cola de envíos escucha únicamente cuando un pedido ha sido 'pedido.creado'.
     */
    @Bean
    public Binding bindingNotificaciones(Queue notificacionesQueue, TopicExchange smartlogixExchange) {
        return BindingBuilder.bind(notificacionesQueue)
                .to(smartlogixExchange)
                .with(ROUTING_KEY_PEDIDO_PATTERN);
    }

    @Bean
    public Binding bindingEnvios(Queue enviosQueue, TopicExchange smartlogixExchange) {
        return BindingBuilder.bind(enviosQueue)
                .to(smartlogixExchange)
                .with(ROUTING_KEY_PEDIDO_CREADO);
    }

    /**
     * 3. Serialización automática de objetos Java a formato JSON en RabbitMQ.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
