package cl.duocuc.smartlogix.rabbitadmin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO de respuesta HTTP neutra para las operaciones del controlador REST.
 * Desacopla completamente la capa de transporte de las clases internas de Spring AMQP.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RabbitAdminResponseDTO {

    private String mensaje;
    private String name;
    private String exchange;
    private String queue;
    private String routingKey;

    public RabbitAdminResponseDTO() {
    }

    public RabbitAdminResponseDTO(String mensaje, String name) {
        this.mensaje = mensaje;
        this.name = name;
    }

    public RabbitAdminResponseDTO(String mensaje, String exchange, String queue, String routingKey) {
        this.mensaje = mensaje;
        this.exchange = exchange;
        this.queue = queue;
        this.routingKey = routingKey;
    }

    public static RabbitAdminResponseDTO ofResource(String mensaje, String name) {
        return new RabbitAdminResponseDTO(mensaje, name);
    }

    public static RabbitAdminResponseDTO ofBinding(String mensaje, String exchange, String queue, String routingKey) {
        return new RabbitAdminResponseDTO(mensaje, exchange, queue, routingKey);
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
    public String getQueue() { return queue; }
    public void setQueue(String queue) { this.queue = queue; }
    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }
}

