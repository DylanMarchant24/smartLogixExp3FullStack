package cl.duocuc.smartlogix.envios.config;

import cl.duocuc.smartlogix.envios.event.PedidoEvent;
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
 * Configuración de RabbitMQ para ms-envios.
 * Nombres de exchanges, colas y routing keys centralizados en application.yml.
 * La cola principal incorpora argumentos avanzados de Dead Letter Exchange (DLX)
 * y el contenedor de listeners opera con acuse de recibo manual (AcknowledgeMode.MANUAL).
 */
@Configuration
public class RabbitMQConfig {

    public static final String DEFAULT_EXCHANGE_NAME = "smartlogix.exchange";
    public static final String DEFAULT_QUEUE_ENVIOS = "smartlogix.envios.queue";
    public static final String DEFAULT_ROUTING_KEY_PEDIDO_CREADO = "pedido.creado";
    public static final String DEFAULT_DLX_NAME = "smartlogix.dlx";
    public static final String DEFAULT_DLQ_ENVIOS = "smartlogix.envios.dlq";
    public static final String DEFAULT_DLQ_ROUTING_KEY = "envios.dead";

    @Value("${mensajeria.exchange.pedidos:" + DEFAULT_EXCHANGE_NAME + "}")
    private String exchangeName = DEFAULT_EXCHANGE_NAME;

    @Value("${mensajeria.colas.envios:" + DEFAULT_QUEUE_ENVIOS + "}")
    private String queueEnvios = DEFAULT_QUEUE_ENVIOS;

    @Value("${mensajeria.routing-keys.envios:" + DEFAULT_ROUTING_KEY_PEDIDO_CREADO + "}")
    private String routingKeyPedidoCreado = DEFAULT_ROUTING_KEY_PEDIDO_CREADO;

    @Value("${mensajeria.exchange.dlx:" + DEFAULT_DLX_NAME + "}")
    private String dlxName = DEFAULT_DLX_NAME;

    @Value("${mensajeria.colas.dlq-envios:" + DEFAULT_DLQ_ENVIOS + "}")
    private String dlqEnvios = DEFAULT_DLQ_ENVIOS;

    @Value("${mensajeria.routing-keys.dlq-envios:" + DEFAULT_DLQ_ROUTING_KEY + "}")
    private String dlqRoutingKey = DEFAULT_DLQ_ROUTING_KEY;

    @Bean
    public TopicExchange smartlogixExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue enviosQueue() {
        return QueueBuilder.durable(queueEnvios)
                .deadLetterExchange(dlxName)
                .deadLetterRoutingKey(dlqRoutingKey)
                .build();
    }

    @Bean
    public Binding bindingEnvios(Queue enviosQueue, TopicExchange smartlogixExchange) {
        return BindingBuilder.bind(enviosQueue)
                .to(smartlogixExchange)
                .with(routingKeyPedidoCreado);
    }

    @Bean
    public DirectExchange smartlogixDeadLetterExchange() {
        return new DirectExchange(dlxName, true, false);
    }

    @Bean
    public Queue enviosDeadLetterQueue() {
        return QueueBuilder.durable(dlqEnvios).build();
    }

    @Bean
    public Binding bindingEnviosDlq(Queue enviosDeadLetterQueue, DirectExchange smartlogixDeadLetterExchange) {
        return BindingBuilder.bind(enviosDeadLetterQueue)
                .to(smartlogixDeadLetterExchange)
                .with(dlqRoutingKey);
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
