package cl.duocuc.smartlogix.pedidos.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuración declarativa de tópicos Apache Kafka para SmartLogix.
 * Define particiones (>= 2), factor de replicación, nivel de retención (retention.ms)
 * y política de limpieza (cleanup.policy) según la rúbrica de evaluación.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${mensajeria.kafka.topics.pedidos-eventos:smartlogix.pedidos.eventos}")
    private String topicPedidosEventos = "smartlogix.pedidos.eventos";

    @Value("${mensajeria.kafka.topics.envios-tracking:smartlogix.envios.tracking}")
    private String topicEnviosTracking = "smartlogix.envios.tracking";

    @Value("${mensajeria.kafka.partitions:3}")
    private int partitions = 3;

    @Value("${mensajeria.kafka.replicas:2}")
    private int replicas = 2;

    @Value("${mensajeria.kafka.retention-ms:604800000}")
    private String retentionMs = "604800000";

    @Value("${mensajeria.kafka.cleanup-policy:delete}")
    private String cleanupPolicy = "delete";

    @Bean
    public NewTopic pedidosEventosTopic() {
        return TopicBuilder.name(topicPedidosEventos)
                .partitions(partitions)
                .replicas(replicas)
                .config(TopicConfig.RETENTION_MS_CONFIG, retentionMs)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, cleanupPolicy)
                .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "1")
                .build();
    }

    @Bean
    public NewTopic enviosTrackingTopic() {
        return TopicBuilder.name(topicEnviosTracking)
                .partitions(partitions)
                .replicas(replicas)
                .config(TopicConfig.RETENTION_MS_CONFIG, retentionMs)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, cleanupPolicy)
                .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "1")
                .build();
    }
}

