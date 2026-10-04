package cl.duocuc.smartlogix.envios.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de infraestructura RabbitMQ para ms-envios:
 * - EXCHANGE: TopicExchange ('smartlogix.exchange').
 * - QUEUE: Cola durable 'smartlogix.envios.queue'.
 * - BINDING: Conecta la cola al TopicExchange con routing key específica 'pedido.creado'.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "smartlogix.exchange";
    public static final String QUEUE_ENVIOS = "smartlogix.envios.queue";
    public static final String ROUTING_KEY_PEDIDO_CREADO = "pedido.creado";

    @Bean
    public TopicExchange smartlogixExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue enviosQueue() {
        return QueueBuilder.durable(QUEUE_ENVIOS).build();
    }

    @Bean
    public Binding bindingEnvios(Queue enviosQueue, TopicExchange smartlogixExchange) {
        return BindingBuilder.bind(enviosQueue)
                .to(smartlogixExchange)
                .with(ROUTING_KEY_PEDIDO_CREADO);
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
