package cl.duocuc.smartlogix.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class CreateBindingRequest {

    @NotBlank(message = "El exchange de origen es obligatorio")
    @Size(max = 255, message = "El exchange no puede superar 255 caracteres")
    @Pattern(
        regexp = "^[a-zA-Z0-9._:-]+$",
        message = "El exchange solo puede contener letras, números, '.', '_', ':' y '-'"
    )
    private String exchange;

    @NotBlank(message = "La cola de destino es obligatoria")
    @Size(max = 255, message = "La cola no puede superar 255 caracteres")
    @Pattern(
        regexp = "^[a-zA-Z0-9._:-]+$",
        message = "La cola solo puede contener letras, números, '.', '_', ':' y '-'"
    )
    private String queue;

    @NotBlank(message = "La routing key es obligatoria")
    @Size(max = 255, message = "La routing key no puede superar 255 caracteres")
    private String routingKey;

    private Map<String, Object> arguments;

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
    public String getQueue() { return queue; }
    public void setQueue(String queue) { this.queue = queue; }
    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }
    public Map<String, Object> getArguments() { return arguments; }
    public void setArguments(Map<String, Object> arguments) { this.arguments = arguments; }
}
