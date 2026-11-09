package cl.duocuc.smartlogix.pedidos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para ms-pedidos.
 * Centraliza los nombres de exchanges, colas y routing keys desde application.yml
 * e incorpora argumentos avanzados de Dead Letter Exchange (DLX) en las colas.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${mensajeria.exchange.pedidos:smartlogix.exchange}")
    private String exchangeName = "smartlogix.exchange";

    @Value("${mensajeria.exchange.dlx:smartlogix.dlx}")
    private String dlxName = "smartlogix.dlx";

    @Value("${mensajeria.colas.notificaciones:smartlogix.notificaciones.queue}")
    private String queueNotificaciones = "smartlogix.notificaciones.queue";

    @Value("${mensajeria.colas.dlq-notificaciones:smartlogix.notificaciones.dlq}")
    private String dlqNotificaciones = "smartlogix.notificaciones.dlq";

    @Value("${mensajeria.colas.envios:smartlogix.envios.queue}")
    private String queueEnvios = "smartlogix.envios.queue";

    @Value("${mensajeria.colas.dlq-envios:smartlogix.envios.dlq}")
    private String dlqEnvios = "smartlogix.envios.dlq";

    @Value("${mensajeria.routing-keys.pedido-creado:pedido.creado}")
    private String routingKeyPedidoCreado = "pedido.creado";

    @Value("${mensajeria.routing-keys.pedido-actualizado:pedido.actualizado}")
    private String routingKeyPedidoActualizado = "pedido.actualizado";

    @Value("${mensajeria.routing-keys.pedido-pattern:pedido.*}")
    private String routingKeyPedidoPattern = "pedido.*";

    @Value("${mensajeria.routing-keys.dlq-notificaciones:notificaciones.dead}")
    private String dlqRoutingKeyNotificaciones = "notificaciones.dead";

    @Value("${mensajeria.routing-keys.dlq-envios:envios.dead}")
    private String dlqRoutingKeyEnvios = "envios.dead";

    @Bean
    public TopicExchange smartlogixExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public DirectExchange smartlogixDeadLetterExchange() {
        return new DirectExchange(dlxName, true, false);
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(queueNotificaciones)
                .deadLetterExchange(dlxName)
                .deadLetterRoutingKey(dlqRoutingKeyNotificaciones)
                .build();
    }

    @Bean
    public Queue enviosQueue() {
        return QueueBuilder.durable(queueEnvios)
                .deadLetterExchange(dlxName)
                .deadLetterRoutingKey(dlqRoutingKeyEnvios)
                .build();
    }

    @Bean
    public Queue notificacionesDeadLetterQueue() {
        return QueueBuilder.durable(dlqNotificaciones).build();
    }

    @Bean
    public Queue enviosDeadLetterQueue() {
        return QueueBuilder.durable(dlqEnvios).build();
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

    @Bean
    public Binding bindingNotificacionesDlq(Queue notificacionesDeadLetterQueue, DirectExchange smartlogixDeadLetterExchange) {
        return BindingBuilder.bind(notificacionesDeadLetterQueue)
                .to(smartlogixDeadLetterExchange)
                .with(dlqRoutingKeyNotificaciones);
    }

    @Bean
    public Binding bindingEnviosDlq(Queue enviosDeadLetterQueue, DirectExchange smartlogixDeadLetterExchange) {
        return BindingBuilder.bind(enviosDeadLetterQueue)
                .to(smartlogixDeadLetterExchange)
                .with(dlqRoutingKeyEnvios);
    }

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
