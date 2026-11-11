package cl.duocuc.smartlogix.auditoria.config;

import cl.duocuc.smartlogix.auditoria.dto.EnvioKafkaEventDTO;
import cl.duocuc.smartlogix.auditoria.dto.PedidoKafkaEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de cliente Apache Kafka, contenedores de consumidores con DTOs tipados,
 * manejo de errores resiliente (reintentos + recuperación) y administración de tópicos.
 */
@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092,localhost:9093}")
    private String bootstrapServers = "localhost:9092,localhost:9093";

    @Value("${spring.kafka.consumer.group-id:smartlogix-auditoria-group}")
    private String groupId = "smartlogix-auditoria-group";

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
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public AdminClient kafkaAdminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        configs.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 5000);
        return AdminClient.create(configs);
    }

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

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {
        return new DefaultErrorHandler((record, exception) -> {
            log.error("[Kafka ErrorHandler - ms-auditoria] Mensaje descartado tras agotar reintentos. topic={}, partition={}, offset={}, causa={}",
                    record.topic(), record.partition(), record.offset(), exception.getMessage());
        }, new FixedBackOff(2000L, 2L));
    }

    @Bean
    public ConsumerFactory<String, PedidoKafkaEventDTO> pedidoConsumerFactory() {
        JsonDeserializer<PedidoKafkaEventDTO> jsonDeserializer = new JsonDeserializer<>(PedidoKafkaEventDTO.class, false);
        jsonDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(),
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PedidoKafkaEventDTO> pedidoKafkaListenerContainerFactory(
            ConsumerFactory<String, PedidoKafkaEventDTO> pedidoConsumerFactory,
            DefaultErrorHandler kafkaErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, PedidoKafkaEventDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(pedidoConsumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        factory.setConcurrency(2);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, EnvioKafkaEventDTO> envioConsumerFactory() {
        JsonDeserializer<EnvioKafkaEventDTO> jsonDeserializer = new JsonDeserializer<>(EnvioKafkaEventDTO.class, false);
        jsonDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(),
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EnvioKafkaEventDTO> envioKafkaListenerContainerFactory(
            ConsumerFactory<String, EnvioKafkaEventDTO> envioConsumerFactory,
            DefaultErrorHandler kafkaErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, EnvioKafkaEventDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(envioConsumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        factory.setConcurrency(2);
        return factory;
    }

    private Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        return props;
    }
}

