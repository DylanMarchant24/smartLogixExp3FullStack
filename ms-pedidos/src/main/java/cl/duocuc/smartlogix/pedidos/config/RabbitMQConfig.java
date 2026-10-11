package cl.duocuc.smartlogix.pedidos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 1. INYECTAMOS LAS VARIABLES CENTRALIZADAS Y ELIMINAMOS LOS TEXTOS QUEMADOS
    @Value("${mensajeria.exchange.pedidos:smartlogix.exchange}")
    private String exchangeName;

    @Value("${mensajeria.colas.notificaciones:smartlogix.notificaciones.queue}")
    private String queueNotificaciones;

    @Value("${mensajeria.colas.envios:smartlogix.envios.queue}")
    private String queueEnvios;

    @Value("${mensajeria.routing-keys.pedido-creado:pedido.creado}")
    private String routingKeyPedidoCreado;

    @Value("${mensajeria.routing-keys.pedido-actualizado:pedido.actualizado}")
    private String routingKeyPedidoActualizado;

    @Value("${mensajeria.routing-keys.pedido-pattern:pedido.*}")
    private String routingKeyPedidoPattern;

    @Bean
    public TopicExchange smartlogixExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(queueNotificaciones).build();
    }

    @Bean
    public Queue enviosQueue() {
        return QueueBuilder.durable(queueEnvios).build();
    }

    @Bean
    public Binding bindingNotificaciones(Queue notificacionesQueue, TopicExchange smartlogixExchange) {
        return BindingBuilder.bind(notificacionesQueue)
                .to(smartlogixExchange)
                .with(routingKeyPedidoPattern);
    }

    @Bean
    public Binding bindingEnvios(Queue enviosQueue, TopicExchange smartlogixExchange) {
        return BindingBuilder.bind(enviosQueue)
                .to(smartlogixExchange)
                .with(routingKeyPedidoCreado);
    }

    // 2. MANTENEMOS LA CONFIGURACIÓN DEL RABBIT TEMPLATE INTACTA
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