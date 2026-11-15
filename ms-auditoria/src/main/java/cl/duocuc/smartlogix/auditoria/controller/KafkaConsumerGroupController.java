package cl.duocuc.smartlogix.auditoria.controller;

import cl.duocuc.smartlogix.auditoria.dto.KafkaConsumerGroupDTO;
import cl.duocuc.smartlogix.auditoria.service.KafkaTopicAdminService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Consumer Groups en Apache Kafka.
 * Permite listar grupos activos, inspeccionar su estado/miembros y eliminar grupos inactivos.
 */
@RestController
@RequestMapping("/api/auditoria/kafka/consumer-groups")
@Validated
@RequiredArgsConstructor
public class KafkaConsumerGroupController {

    private static final String GROUP_ID_REGEX = "^[a-zA-Z0-9._-]+$";

    private final KafkaTopicAdminService kafkaTopicAdminService;

    @GetMapping
    public ResponseEntity<List<KafkaConsumerGroupDTO>> listarConsumerGroups() {
        return ResponseEntity.ok(kafkaTopicAdminService.listarConsumerGroups());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<KafkaConsumerGroupDTO> describirConsumerGroup(
            @PathVariable
            @NotBlank(message = "El ID del consumer group es obligatorio")
            @Pattern(regexp = GROUP_ID_REGEX, message = "ID de consumer group inválido")
            String groupId) {
        return ResponseEntity.ok(kafkaTopicAdminService.describirConsumerGroup(groupId));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> eliminarConsumerGroup(
            @PathVariable
            @NotBlank(message = "El ID del consumer group es obligatorio")
            @Pattern(regexp = GROUP_ID_REGEX, message = "ID de consumer group inválido")
            String groupId) {
        kafkaTopicAdminService.eliminarConsumerGroup(groupId);
        return ResponseEntity.noContent().build();
    }
}

