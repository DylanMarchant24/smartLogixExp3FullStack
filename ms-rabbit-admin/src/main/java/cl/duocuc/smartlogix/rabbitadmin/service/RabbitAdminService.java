package cl.duocuc.smartlogix.rabbitadmin.service;

import cl.duocuc.smartlogix.rabbitadmin.dto.CreateBindingRequest;
import cl.duocuc.smartlogix.rabbitadmin.dto.CreateExchangeRequest;
import cl.duocuc.smartlogix.rabbitadmin.dto.CreateQueueRequest;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class RabbitAdminService {

    private final RabbitAdmin rabbitAdmin;

    public RabbitAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    public void createQueue(CreateQueueRequest request) {

        Queue queue;

        if (request.isExclusive() && request.isAutoDelete()) {
            queue = QueueBuilder
                    .durable(request.getName())
                    .exclusive()
                    .autoDelete()
                    .withArguments(defaultArguments(request.getArguments()))
                    .build();

        } else if (request.isExclusive()) {
            queue = QueueBuilder
                    .durable(request.getName())
                    .exclusive()
                    .withArguments(defaultArguments(request.getArguments()))
                    .build();

        } else if (request.isAutoDelete()) {
            queue = QueueBuilder
                    .durable(request.getName())
                    .autoDelete()
                    .withArguments(defaultArguments(request.getArguments()))
                    .build();

        } else {
            queue = QueueBuilder
                    .durable(request.getName())
                    .withArguments(defaultArguments(request.getArguments()))
                    .build();
        }

        rabbitAdmin.declareQueue(queue);
    }

    public void deleteQueue(String name) {
        rabbitAdmin.deleteQueue(name);
    }

    public void createExchange(CreateExchangeRequest request) {
        Exchange exchange = buildExchange(request);
        rabbitAdmin.declareExchange(exchange);
    }

    public void deleteExchange(String name) {
        rabbitAdmin.deleteExchange(name);
    }

    public void createBinding(CreateBindingRequest request) {
        Binding binding = new Binding(
                request.getQueue(),
                Binding.DestinationType.QUEUE,
                request.getExchange(),
                request.getRoutingKey(),
                defaultArguments(request.getArguments())
        );

        rabbitAdmin.declareBinding(binding);
    }

    public void deleteBinding(String exchange, String queue, String routingKey) {
        Binding binding = new Binding(
                queue,
                Binding.DestinationType.QUEUE,
                exchange,
                routingKey,
                Collections.emptyMap()
        );

        rabbitAdmin.removeBinding(binding);
    }

    private Exchange buildExchange(CreateExchangeRequest request) {
        String type = request.getType().toLowerCase();

        return switch (type) {
            case "direct" -> new DirectExchange(
                    request.getName(),
                    request.isDurable(),
                    request.isAutoDelete(),
                    defaultArguments(request.getArguments())
            );
            case "topic" -> new TopicExchange(
                    request.getName(),
                    request.isDurable(),
                    request.isAutoDelete(),
                    defaultArguments(request.getArguments())
            );
            case "fanout" -> new FanoutExchange(
                    request.getName(),
                    request.isDurable(),
                    request.isAutoDelete(),
                    defaultArguments(request.getArguments())
            );
            case "headers" -> new HeadersExchange(
                    request.getName(),
                    request.isDurable(),
                    request.isAutoDelete(),
                    defaultArguments(request.getArguments())
            );
            default -> throw new IllegalArgumentException(
                    "Tipo de exchange no soportado: " + request.getType()
            );
        };
    }

    private java.util.Map<String, Object> defaultArguments(java.util.Map<String, Object> arguments) {
        return arguments == null ? Collections.emptyMap() : arguments;
    }
}
