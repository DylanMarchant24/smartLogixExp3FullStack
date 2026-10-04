package cl.duocuc.smartlogix.notificaciones.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de infraestructura RabbitMQ para ms-notificaciones:
 * - EXCHANGE: TopicExchange ('smartlogix.exchange').
 * - QUEUE: Cola durable 'smartlogix.notificaciones.queue'.
 * - BINDING: Conecta la cola al TopicExchange con routing key 'pedido.*'.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "smartlogix.exchange";
    public static final String QUEUE_NOTIFICACIONES = "smartlogix.notificaciones.queue";
    public static final String ROUTING_KEY_PEDIDO_PATTERN = "pedido.*";

    @Bean
    public TopicExchange smartlogixExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(QUEUE_NOTIFICACIONES).build();
    }

    @Bean
    public Binding bindingNotificaciones(Queue notificacionesQueue, TopicExchange smartlogixExchange) {
        return BindingBuilder.bind(notificacionesQueue)
                .to(smartlogixExchange)
                .with(ROUTING_KEY_PEDIDO_PATTERN);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
