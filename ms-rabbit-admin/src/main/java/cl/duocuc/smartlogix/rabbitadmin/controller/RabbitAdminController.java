package cl.duocuc.smartlogix.rabbitadmin.controller;

import cl.duocuc.smartlogix.rabbitadmin.dto.CreateBindingRequest;
import cl.duocuc.smartlogix.rabbitadmin.dto.CreateExchangeRequest;
import cl.duocuc.smartlogix.rabbitadmin.dto.CreateQueueRequest;
import cl.duocuc.smartlogix.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rabbit-admin")
@Validated
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    @PostMapping("/queues")
    public ResponseEntity<Map<String, String>> createQueue(
            @Valid @RequestBody CreateQueueRequest request) {

        rabbitAdminService.createQueue(request);

        return ResponseEntity.status(201).body(
                Map.of("mensaje", "Cola creada correctamente", "name", request.getName())
        );
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Void> deleteQueue(@PathVariable @NotBlank String name) {
        rabbitAdminService.deleteQueue(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exchanges")
    public ResponseEntity<Map<String, String>> createExchange(
            @Valid @RequestBody CreateExchangeRequest request) {

        rabbitAdminService.createExchange(request);

        return ResponseEntity.status(201).body(
                Map.of("mensaje", "Exchange creado correctamente", "name", request.getName())
        );
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Void> deleteExchange(@PathVariable @NotBlank String name) {
        rabbitAdminService.deleteExchange(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bindings")
    public ResponseEntity<Map<String, String>> createBinding(
            @Valid @RequestBody CreateBindingRequest request) {

        rabbitAdminService.createBinding(request);

        return ResponseEntity.status(201).body(
                Map.of(
                        "mensaje", "Binding creado correctamente",
                        "exchange", request.getExchange(),
                        "queue", request.getQueue(),
                        "routingKey", request.getRoutingKey()
                )
        );
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<Void> deleteBinding(
            @RequestParam @NotBlank String exchange,
            @RequestParam @NotBlank String queue,
            @RequestParam @NotBlank String routingKey) {

        rabbitAdminService.deleteBinding(exchange, queue, routingKey);
        return ResponseEntity.noContent().build();
    }
}
