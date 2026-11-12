package cl.duocuc.smartlogix.rabbitadmin.dto;

import jakarta.validation.constraints.*;

public class CreateKafkaTopicRequest {

    @NotBlank(message = "El nombre del tópico no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre del tópico solo puede contener letras, números, puntos, guiones y guiones bajos")
    private String nombre;

    @Min(value = 1, message = "Debe tener al menos 1 partición")
    @Max(value = 100, message = "El número máximo de particiones es 100")
    private int particiones = 3;

    @Min(value = 1, message = "El factor de replicación mínimo es 1")
    @Max(value = 3, message = "El factor de replicación máximo para este clúster es 3")
    private short factorReplicacion = 3;

    @NotNull(message = "El tiempo de retención no puede ser nulo")
    @Min(value = 1000, message = "La retención mínima es de 1000 ms")
    private Long retentionMs = 604800000L; // 7 días

    @NotBlank(message = "La política de limpieza no puede estar vacía")
    @Pattern(regexp = "^(delete|compact)$", message = "La política de limpieza debe ser 'delete' o 'compact'")
    private String cleanupPolicy = "delete";

    public CreateKafkaTopicRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getParticiones() { return particiones; }
    public void setParticiones(int particiones) { this.particiones = particiones; }
    public short getFactorReplicacion() { return factorReplicacion; }
    public void setFactorReplicacion(short factorReplicacion) { this.factorReplicacion = factorReplicacion; }
    public Long getRetentionMs() { return retentionMs; }
    public void setRetentionMs(Long retentionMs) { this.retentionMs = retentionMs; }
    public String getCleanupPolicy() { return cleanupPolicy; }
    public void setCleanupPolicy(String cleanupPolicy) { this.cleanupPolicy = cleanupPolicy; }
}