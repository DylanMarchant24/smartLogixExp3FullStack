package cl.duocuc.smartlogix.auditoria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO con la información detallada de un grupo de consumidores (Consumer Group) en Apache Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaConsumerGroupDTO {

    private String groupId;
    private String state;
    private int memberCount;
    private String coordinator;
    private Set<String> assignedTopics;
}

