package cl.duocuc.smartlogix.envios;

import cl.duocuc.smartlogix.envios.config.RabbitMQConfig;
import cl.duocuc.smartlogix.envios.consumer.PedidoEnvioListener;
import cl.duocuc.smartlogix.envios.dto.EnvioDTO;
import cl.duocuc.smartlogix.envios.event.PedidoEvent;
import cl.duocuc.smartlogix.envios.service.EnvioService;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQEnviosTest {

    @Mock
    private EnvioService envioService;

    @Mock
    private Channel channel;

    @InjectMocks
    private PedidoEnvioListener listener;

    @Test
    void colaEnvios_tieneArgumentosAvanzadosDeDlx() {
        RabbitMQConfig config = new RabbitMQConfig();
        Queue queue = config.enviosQueue();

        assertEquals(RabbitMQConfig.DEFAULT_QUEUE_ENVIOS, queue.getName());
        assertTrue(queue.isDurable());
        assertEquals(RabbitMQConfig.DEFAULT_DLX_NAME, queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals(RabbitMQConfig.DEFAULT_DLQ_ROUTING_KEY, queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void listenerContainerFactory_tieneAckManual() {
        RabbitMQConfig config = new RabbitMQConfig();
        ConnectionFactory cf = mock(ConnectionFactory.class);
        var factory = config.rabbitListenerContainerFactory(cf, config.jsonMessageConverter());
        var container = factory.createListenerContainer();
        assertEquals(AcknowledgeMode.MANUAL, container.getAcknowledgeMode());
    }

    @Test
    void procesarPedidoCreado_exito_ejecutaBasicAck() throws IOException {
        PedidoEvent event = PedidoEvent.builder()
                .pedidoId(10L)
                .clienteEmail("cliente@smartlogix.cl")
                .build();

        EnvioDTO creado = new EnvioDTO();
        creado.setPedidoId(10L);
        creado.setCodigoSeguimiento("TRK-001");

        when(envioService.listarPorPedido(10L)).thenReturn(Collections.emptyList());
        when(envioService.crearEnvio(any(EnvioDTO.class))).thenReturn(creado);

        listener.procesarPedidoCreado(event, channel, 5L);

        verify(channel, times(1)).basicAck(5L, false);
    }

    @Test
    void procesarPedidoCreado_error_ejecutaBasicNackSinRequeue() throws IOException {
        PedidoEvent event = PedidoEvent.builder()
                .pedidoId(10L)
                .build();

        when(envioService.listarPorPedido(10L)).thenThrow(new RuntimeException("Fallo DB"));

        listener.procesarPedidoCreado(event, channel, 5L);

        verify(channel, times(1)).basicNack(5L, false, false);
    }
}

