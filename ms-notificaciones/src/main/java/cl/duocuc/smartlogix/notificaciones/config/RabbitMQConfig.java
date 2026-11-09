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
 * Configuración de infraestructura RabbitMQ para ms-notificaciones.
 * Nombres de exchanges, colas y routing keys centralizados en application.yml.
 * La cola principal declara argumentos avanzados de Dead Letter Exchange (DLX)
 * y el contenedor de listeners opera con acuse de recibo manual (AcknowledgeMode.MANUAL).
 */
@Configuration
public class RabbitMQConfig {

    public static final String DEFAULT_EXCHANGE_NAME = "smartlogix.exchange";
    public static final String DEFAULT_QUEUE_NOTIFICACIONES = "smartlogix.notificaciones.queue";
    public static final String DEFAULT_ROUTING_KEY_PATTERN = "pedido.*";

    @Value("${mensajeria.exchange.pedidos:" + DEFAULT_EXCHANGE_NAME + "}")
    private String exchangeName = DEFAULT_EXCHANGE_NAME;

    @Value("${mensajeria.colas.notificaciones:" + DEFAULT_QUEUE_NOTIFICACIONES + "}")
    private String queueNotificaciones = DEFAULT_QUEUE_NOTIFICACIONES;

    @Value("${mensajeria.routing-keys.notificaciones:" + DEFAULT_ROUTING_KEY_PATTERN + "}")
    private String routingKeyPedidoPattern = DEFAULT_ROUTING_KEY_PATTERN;

    @Value("${mensajeria.exchange.dlx:" + RabbitMQDlqConfig.DEFAULT_DLX_NAME + "}")
    private String dlxName = RabbitMQDlqConfig.DEFAULT_DLX_NAME;

    @Value("${mensajeria.routing-keys.dlq-notificaciones:" + RabbitMQDlqConfig.DEFAULT_DLQ_ROUTING_KEY + "}")
    private String dlqRoutingKey = RabbitMQDlqConfig.DEFAULT_DLQ_ROUTING_KEY;

    @Bean
    public TopicExchange smartlogixExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(queueNotificaciones)
                .deadLetterExchange(dlxName)
                .deadLetterRoutingKey(dlqRoutingKey)
                .build();
    }

    @Bean
    public Binding bindingNotificaciones(Queue notificacionesQueue, TopicExchange smartlogixExchange) {
        return BindingBuilder.bind(notificacionesQueue)
                .to(smartlogixExchange)
                .with(routingKeyPedidoPattern);
    }

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
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
