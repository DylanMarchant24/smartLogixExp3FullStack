package cl.duocuc.smartlogix.auditoria.controller;

import cl.duocuc.smartlogix.auditoria.dto.CreateKafkaTopicRequest;
import cl.duocuc.smartlogix.auditoria.dto.KafkaTopicInfoDTO;
import cl.duocuc.smartlogix.auditoria.service.KafkaTopicAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * Controlador REST para administración de tópicos Apache Kafka.
 * Permite crear, listar, inspeccionar configuraciones (retention.ms, cleanup.policy, particiones, réplicas),
 * actualizar parámetros y eliminar tópicos.
 */
@RestController
@RequestMapping("/api/auditoria/kafka/topics")
@Validated
@RequiredArgsConstructor
public class KafkaTopicAdminController {

    private static final String TOPIC_NAME_REGEX = "^[a-zA-Z0-9._-]+$";

    private final KafkaTopicAdminService kafkaTopicAdminService;

    @GetMapping
    public ResponseEntity<Set<String>> listarTopicos() {
        return ResponseEntity.ok(kafkaTopicAdminService.listarTopicos());
    }

    @GetMapping("/{name}")
    public ResponseEntity<KafkaTopicInfoDTO> describirTopico(
            @PathVariable
            @NotBlank(message = "El nombre del tópico es obligatorio")
            @Pattern(regexp = TOPIC_NAME_REGEX, message = "Nombre de tópico inválido")
            String name) {
        return ResponseEntity.ok(kafkaTopicAdminService.describirTopico(name));
    }

    @PostMapping
    public ResponseEntity<KafkaTopicInfoDTO> crearTopico(
            @Valid @RequestBody CreateKafkaTopicRequest request) {
        KafkaTopicInfoDTO creado = kafkaTopicAdminService.crearTopico(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{name}/config")
    public ResponseEntity<Map<String, String>> actualizarConfiguracion(
            @PathVariable
            @NotBlank
            @Pattern(regexp = TOPIC_NAME_REGEX, message = "Nombre de tópico inválido")
            String name,
            @RequestParam(defaultValue = "604800000") @Min(60000) long retentionMs,
            @RequestParam(defaultValue = "delete")
            @Pattern(regexp = "^(delete|compact|compact,delete)$") String cleanupPolicy) {
        return ResponseEntity.ok(
                kafkaTopicAdminService.actualizarConfiguracionTopico(name, retentionMs, cleanupPolicy)
        );
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> eliminarTopico(
            @PathVariable
            @NotBlank
            @Pattern(regexp = TOPIC_NAME_REGEX, message = "Nombre de tópico inválido")
            String name) {
        kafkaTopicAdminService.eliminarTopico(name);
        return ResponseEntity.noContent().build();
    }
}

