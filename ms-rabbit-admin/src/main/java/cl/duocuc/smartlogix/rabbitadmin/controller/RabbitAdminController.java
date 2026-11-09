package cl.duocuc.smartlogix.rabbitadmin.controller;

import cl.duocuc.smartlogix.rabbitadmin.dto.CreateBindingRequest;
import cl.duocuc.smartlogix.rabbitadmin.dto.CreateExchangeRequest;
import cl.duocuc.smartlogix.rabbitadmin.dto.CreateQueueRequest;
import cl.duocuc.smartlogix.rabbitadmin.dto.RabbitAdminResponseDTO;
import cl.duocuc.smartlogix.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para administración de recursos RabbitMQ.
 * Valida estrictamente nombres de colas, exchanges y routing keys mediante Bean Validation (@Valid, @NotBlank, @Pattern)
 * y retorna únicamente DTOs y respuestas HTTP neutras (sin exponer clases internas de Spring AMQP).
 */
@RestController
@RequestMapping("/api/rabbit-admin")
@Validated
public class RabbitAdminController {

    private static final String RABBIT_NAME_REGEX = "^[a-zA-Z0-9._:-]+$";
    private static final String RABBIT_ROUTING_KEY_REGEX = "^[a-zA-Z0-9._:#*-]+$";

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    @PostMapping("/queues")
    public ResponseEntity<RabbitAdminResponseDTO> createQueue(
            @Valid @RequestBody CreateQueueRequest request) {

        rabbitAdminService.createQueue(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                RabbitAdminResponseDTO.ofResource("Cola creada correctamente", request.getName())
        );
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Void> deleteQueue(
            @PathVariable
            @NotBlank(message = "El nombre de la cola es obligatorio")
            @Size(max = 255, message = "El nombre de la cola no puede superar 255 caracteres")
            @Pattern(regexp = RABBIT_NAME_REGEX, message = "El nombre de la cola contiene caracteres no permitidos por RabbitMQ")
            String name) {

        rabbitAdminService.deleteQueue(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exchanges")
    public ResponseEntity<RabbitAdminResponseDTO> createExchange(
            @Valid @RequestBody CreateExchangeRequest request) {

        rabbitAdminService.createExchange(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                RabbitAdminResponseDTO.ofResource("Exchange creado correctamente", request.getName())
        );
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Void> deleteExchange(
            @PathVariable
            @NotBlank(message = "El nombre del exchange es obligatorio")
            @Size(max = 255, message = "El nombre del exchange no puede superar 255 caracteres")
            @Pattern(regexp = RABBIT_NAME_REGEX, message = "El nombre del exchange contiene caracteres no permitidos por RabbitMQ")
            String name) {

        rabbitAdminService.deleteExchange(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bindings")
    public ResponseEntity<RabbitAdminResponseDTO> createBinding(
            @Valid @RequestBody CreateBindingRequest request) {

        rabbitAdminService.createBinding(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                RabbitAdminResponseDTO.ofBinding(
                        "Binding creado correctamente",
                        request.getExchange(),
                        request.getQueue(),
                        request.getRoutingKey()
                )
        );
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<Void> deleteBinding(
            @RequestParam
            @NotBlank(message = "El exchange es obligatorio")
            @Size(max = 255)
            @Pattern(regexp = RABBIT_NAME_REGEX, message = "El exchange contiene caracteres no permitidos por RabbitMQ")
            String exchange,
            @RequestParam
            @NotBlank(message = "El nombre de la cola es obligatorio")
            @Size(max = 255)
            @Pattern(regexp = RABBIT_NAME_REGEX, message = "El nombre de la cola contiene caracteres no permitidos por RabbitMQ")
            String queue,
            @RequestParam
            @NotBlank(message = "La routing key es obligatoria")
            @Size(max = 255)
            @Pattern(regexp = RABBIT_ROUTING_KEY_REGEX, message = "La routing key contiene caracteres no permitidos por RabbitMQ")
            String routingKey) {

        rabbitAdminService.deleteBinding(exchange, queue, routingKey);
        return ResponseEntity.noContent().build();
    }
}
