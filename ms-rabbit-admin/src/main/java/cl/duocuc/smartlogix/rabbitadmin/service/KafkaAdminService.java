package cl.duocuc.smartlogix.rabbitadmin.service;

import cl.duocuc.smartlogix.rabbitadmin.dto.ConsumerGroupInfoDTO;
import cl.duocuc.smartlogix.rabbitadmin.dto.CreateKafkaTopicRequest;
import cl.duocuc.smartlogix.rabbitadmin.dto.KafkaTopicInfoDTO;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KafkaAdminService {

    private final KafkaAdmin kafkaAdmin;

    public KafkaAdminService(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    public void crearTopico(CreateKafkaTopicRequest request) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            NewTopic newTopic = new NewTopic(request.getNombre(), request.getParticiones(), request.getFactorReplicacion());
            adminClient.createTopics(Collections.singletonList(newTopic));
        }
    }

    public List<String> listarTopicos() throws Exception {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            return adminClient.listTopics().names().get().stream().collect(Collectors.toList());
        }
    }

    public KafkaTopicInfoDTO obtenerDetalleTopico(String nombre) {
        return new KafkaTopicInfoDTO(nombre); // Simplificado para la estructura base
    }

    public void eliminarTopico(String nombre) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.deleteTopics(Collections.singletonList(nombre));
        }
    }

    public List<ConsumerGroupInfoDTO> listarGruposConsumidores() throws Exception {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            return adminClient.listConsumerGroups().valid().get().stream()
                    .map(group -> new ConsumerGroupInfoDTO(group.groupId()))
                    .collect(Collectors.toList());
        }
    }

    public void eliminarGrupoConsumidor(String groupId) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.deleteConsumerGroups(Collections.singletonList(groupId));
        }
    }
}