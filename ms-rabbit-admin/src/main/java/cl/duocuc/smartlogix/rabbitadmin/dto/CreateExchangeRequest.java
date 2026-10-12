package cl.duocuc.smartlogix.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class CreateExchangeRequest {

    @NotBlank(message = "El nombre del exchange es obligatorio")
    @Size(max = 255, message = "El nombre del exchange no puede superar 255 caracteres")
    @Pattern(
        regexp = "^[a-zA-Z0-9._:-]+$",
        message = "El nombre del exchange solo puede contener letras, números, '.', '_', ':' y '-'"
    )
    private String name;

    @NotBlank(message = "El tipo de exchange es obligatorio")
    @Pattern(
        regexp = "(?i)^(direct|topic|fanout|headers)$",
        message = "El tipo debe ser direct, topic, fanout o headers"
    )
    private String type;

    private boolean durable = true;
    private boolean autoDelete = false;
    private Map<String, Object> arguments;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isDurable() { return durable; }
    public void setDurable(boolean durable) { this.durable = durable; }
    public boolean isAutoDelete() { return autoDelete; }
    public void setAutoDelete(boolean autoDelete) { this.autoDelete = autoDelete; }
    public Map<String, Object> getArguments() { return arguments; }
    public void setArguments(Map<String, Object> arguments) { this.arguments = arguments; }
}
