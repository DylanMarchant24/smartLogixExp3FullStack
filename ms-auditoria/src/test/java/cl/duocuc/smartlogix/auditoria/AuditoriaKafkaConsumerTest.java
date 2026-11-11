package cl.duocuc.smartlogix.auditoria;

import cl.duocuc.smartlogix.auditoria.config.KafkaConfig;
import cl.duocuc.smartlogix.auditoria.consumer.AuditoriaKafkaConsumer;
import cl.duocuc.smartlogix.auditoria.dto.EnvioKafkaEventDTO;
import cl.duocuc.smartlogix.auditoria.dto.PedidoKafkaEventDTO;
import cl.duocuc.smartlogix.auditoria.model.Auditoria;
import cl.duocuc.smartlogix.auditoria.service.AuditoriaService;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditoriaKafkaConsumerTest {

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private AuditoriaKafkaConsumer consumer;

    @Test
    void topicosKafka_tienenParticionesReplicasYRetencionConfiguradas() {
        KafkaConfig config = new KafkaConfig();
        NewTopic pedidosTopic = config.pedidosEventosTopic();
        NewTopic enviosTopic = config.enviosTrackingTopic();

        assertEquals("smartlogix.pedidos.eventos", pedidosTopic.name());
        assertEquals(3, pedidosTopic.numPartitions());
        assertEquals((short) 2, pedidosTopic.replicationFactor());
        assertEquals("604800000", pedidosTopic.configs().get(TopicConfig.RETENTION_MS_CONFIG));
        assertEquals("delete", pedidosTopic.configs().get(TopicConfig.CLEANUP_POLICY_CONFIG));

        assertEquals("smartlogix.envios.tracking", enviosTopic.name());
        assertEquals(3, enviosTopic.numPartitions());
        assertEquals((short) 2, enviosTopic.replicationFactor());
    }

    @Test
    void consumirEventoPedido_valido_guardaRegistroAuditoria() {
        PedidoKafkaEventDTO event = PedidoKafkaEventDTO.builder()
                .pedidoId(42L)
                .skuProducto("SKU-100")
                .cantidad(3)
                .clienteEmail("dylan@smartlogix.cl")
                .estado("APROBADO")
                .tipoEvento("PEDIDO_CREADO")
                .mensaje("Pedido aprobado")
                .build();

        consumer.consumirEventoPedido(event, 1, 10L);

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaService, times(1)).guardarAuditoria(captor.capture());

        Auditoria guardada = captor.getValue();
        assertEquals("PEDIDO_CREADO", guardada.getAccion());
        assertEquals("PEDIDO", guardada.getEntidad());
        assertEquals(42L, guardada.getEntidadId());
        assertEquals("dylan@smartlogix.cl", guardada.getUsuarioResponsable());
        assertTrue(guardada.getDetalles().contains("SKU=SKU-100"));
    }

    @Test
    void consumirEventoEnvio_valido_guardaRegistroAuditoria() {
        EnvioKafkaEventDTO event = EnvioKafkaEventDTO.builder()
                .envioId(99L)
                .pedidoId(42L)
                .codigoSeguimiento("SL-ABCDEF12")
                .transportista("SmartLogix Express")
                .direccionDestino("Av. Providencia 1234")
                .estado("EN_TRANSITO")
                .tipoEvento("ENVIO_ESTADO_ACTUALIZADO")
                .mensaje("En camino")
                .build();

        consumer.consumirEventoEnvio(event, 2, 15L);

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaService, times(1)).guardarAuditoria(captor.capture());

        Auditoria guardada = captor.getValue();
        assertEquals("ENVIO_ESTADO_ACTUALIZADO", guardada.getAccion());
        assertEquals("ENVIO", guardada.getEntidad());
        assertEquals(99L, guardada.getEntidadId());
        assertTrue(guardada.getDetalles().contains("SL-ABCDEF12"));
    }

    @Test
    void consumirEventoPedido_invalido_lanzaExcepcionParaErrorHandler() {
        PedidoKafkaEventDTO invalido = new PedidoKafkaEventDTO();
        assertThrows(IllegalArgumentException.class,
                () -> consumer.consumirEventoPedido(invalido, 0, 1L));
    }
}

