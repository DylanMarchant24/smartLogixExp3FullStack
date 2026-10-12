package cl.duocuc.smartlogix.rabbitadmin.config;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitAdminConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {

        return new RabbitAdmin(connectionFactory);
    }
}