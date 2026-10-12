package cl.duocuc.smartlogix.notificaciones.config;

import cl.duocuc.smartlogix.notificaciones.event.PedidoEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de infraestructura RabbitMQ para ms-notificaciones:
 * - EXCHANGE: TopicExchange ('smartlogix.exchange').
 * - QUEUE: Cola durable 'smartlogix.notificaciones.queue' (con DLX configurado hacia RabbitMQDlqConfig).
 * - BINDING: Conecta la cola al TopicExchange con routing key 'pedido.*'.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "smartlogix.exchange";
    public static final String QUEUE_NOTIFICACIONES = "smartlogix.notificaciones.queue";
    public static final String ROUTING_KEY_PEDIDO_PATTERN = "pedido.*";

    // 1. Inyectamos variables centralizadas desde application.properties (con valores por defecto)
    @Value("${mensajeria.exchange.pedidos:smartlogix.exchange}")
    private String exchangeName = EXCHANGE_NAME;

    @Value("${mensajeria.colas.notificaciones:smartlogix.notificaciones.queue}")
    private String queueNotificaciones = QUEUE_NOTIFICACIONES;

    @Value("${mensajeria.routing-keys.notificaciones:pedido.*}")
    private String routingKeyPedidoPattern = ROUTING_KEY_PEDIDO_PATTERN;

    @Bean
    public TopicExchange smartlogixExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(queueNotificaciones)
                .deadLetterExchange(RabbitMQDlqConfig.DLX_NAME)
                .deadLetterRoutingKey(RabbitMQDlqConfig.DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding bindingNotificaciones(Queue notificacionesQueue, TopicExchange smartlogixExchange) {
        return BindingBuilder.bind(notificacionesQueue)
                .to(smartlogixExchange)
                .with(routingKeyPedidoPattern);
    }

    // 2. Conversor JSON con soporte cruzado de paquetes para PedidoEvent
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        classMapper.setDefaultType(PedidoEvent.class);
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("pedidoEvent", PedidoEvent.class);
        idClassMapping.put("cl.duocuc.smartlogix.pedidos.event.PedidoEvent", PedidoEvent.class);
        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}