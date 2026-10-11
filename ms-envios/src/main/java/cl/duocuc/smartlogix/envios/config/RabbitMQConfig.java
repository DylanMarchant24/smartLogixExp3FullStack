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

@Configuration
public class RabbitMQConfig {

    // 1. INYECTAMOS LAS VARIABLES (Eliminamos los public static final String)
    @Value("${mensajeria.exchange.pedidos:smartlogix.exchange}")
    private String exchangeName;

    @Value("${mensajeria.colas.envios:smartlogix.envios.queue}")
    private String queueEnvios;

    @Value("${mensajeria.routing-keys.envios:pedido.creado}")
    private String routingKeyPedidoCreado;

    @Bean
    public TopicExchange smartlogixExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue enviosQueue() {
        return QueueBuilder.durable(queueEnvios).build();
    }

    @Bean
    public Binding bindingEnvios(Queue enviosQueue, TopicExchange smartlogixExchange) {
        return BindingBuilder.bind(enviosQueue)
                .to(smartlogixExchange)
                .with(routingKeyPedidoCreado);
    }

    // 2. MANTENEMOS INTACTA LA LÓGICA DE DYLAN HACIA ABAJO
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