package cl.duocuc.smartlogix.auditoria;

import cl.duocuc.smartlogix.auditoria.dto.KafkaConsumerGroupDTO;
import cl.duocuc.smartlogix.auditoria.service.KafkaTopicAdminService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.DeleteConsumerGroupsResult;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.admin.MemberAssignment;
import org.apache.kafka.clients.admin.MemberDescription;
import org.apache.kafka.common.ConsumerGroupState;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaTopicAdminServiceTest {

    @Mock
    private AdminClient adminClient;

    @InjectMocks
    private KafkaTopicAdminService kafkaTopicAdminService;

    @Test
    void listarConsumerGroups_retornaEstadoMiembrosYTopicos() {
        ListConsumerGroupsResult listResult = mock(ListConsumerGroupsResult.class);
        when(listResult.valid()).thenReturn(KafkaFuture.completedFuture(
                List.of(new ConsumerGroupListing("smartlogix-auditoria-group", false))
        ));
        when(adminClient.listConsumerGroups()).thenReturn(listResult);

        MemberAssignment assignment = new MemberAssignment(Set.of(
                new TopicPartition("smartlogix.pedidos.eventos", 0),
                new TopicPartition("smartlogix.envios.tracking", 1)
        ));
        MemberDescription member = new MemberDescription("member-1", Optional.empty(), "client-1", "localhost", assignment);
        ConsumerGroupDescription desc = new ConsumerGroupDescription(
                "smartlogix-auditoria-group",
                false,
                List.of(member),
                "range",
                ConsumerGroupState.STABLE,
                new Node(1, "kafka-1", 29092)
        );

        DescribeConsumerGroupsResult describeResult = mock(DescribeConsumerGroupsResult.class);
        when(describeResult.all()).thenReturn(KafkaFuture.completedFuture(
                Map.of("smartlogix-auditoria-group", desc)
        ));
        when(adminClient.describeConsumerGroups(List.of("smartlogix-auditoria-group"))).thenReturn(describeResult);

        List<KafkaConsumerGroupDTO> groups = kafkaTopicAdminService.listarConsumerGroups();

        assertEquals(1, groups.size());
        KafkaConsumerGroupDTO dto = groups.get(0);
        assertEquals("smartlogix-auditoria-group", dto.getGroupId());
        assertEquals("Stable", dto.getState());
        assertEquals(1, dto.getMemberCount());
        assertEquals("kafka-1:29092", dto.getCoordinator());
        assertTrue(dto.getAssignedTopics().contains("smartlogix.pedidos.eventos"));
        assertTrue(dto.getAssignedTopics().contains("smartlogix.envios.tracking"));
    }

    @Test
    void eliminarConsumerGroup_invocaDeleteYEsperaCompletitud() {
        DeleteConsumerGroupsResult deleteResult = mock(DeleteConsumerGroupsResult.class);
        when(deleteResult.all()).thenReturn(KafkaFuture.completedFuture(null));
        when(adminClient.deleteConsumerGroups(List.of("grupo-prueba"))).thenReturn(deleteResult);

        kafkaTopicAdminService.eliminarConsumerGroup("grupo-prueba");

        verify(adminClient, times(1)).deleteConsumerGroups(List.of("grupo-prueba"));
    }
}
