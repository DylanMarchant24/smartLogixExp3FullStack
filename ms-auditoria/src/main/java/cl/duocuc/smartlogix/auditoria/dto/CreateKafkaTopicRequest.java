package cl.duocuc.smartlogix.auditoria.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de creación o actualización de tópicos Apache Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateKafkaTopicRequest {

    @NotBlank(message = "El nombre del tópico es obligatorio")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+$",
        message = "El nombre del tópico solo puede contener letras, números, '.', '_' y '-'"
    )
    private String name;

    @Min(value = 1, message = "El número de particiones debe ser al menos 1")
    @Max(value = 32, message = "El número de particiones no debe superar 32")
    @Builder.Default
    private int partitions = 3;

    @Min(value = 1, message = "El factor de replicación debe ser al menos 1")
    @Max(value = 3, message = "El factor de replicación no debe superar 3")
    @Builder.Default
    private short replicationFactor = 2;

    @Min(value = 60000, message = "El tiempo de retención mínimo es 60000 ms (1 minuto)")
    @Builder.Default
    private long retentionMs = 604800000L; // 7 días por defecto

    @Pattern(
        regexp = "^(delete|compact|compact,delete)$",
        message = "cleanupPolicy debe ser 'delete', 'compact' o 'compact,delete'"
    )
    @Builder.Default
    private String cleanupPolicy = "delete";
}

