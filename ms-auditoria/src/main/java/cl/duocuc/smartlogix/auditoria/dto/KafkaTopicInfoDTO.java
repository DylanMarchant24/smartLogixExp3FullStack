package cl.duocuc.smartlogix.auditoria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO de respuesta con metadatos de administración de un tópico de Apache Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaTopicInfoDTO {

    private String name;
    private int partitions;
    private int replicationFactor;
    private Map<String, String> configs;
}

