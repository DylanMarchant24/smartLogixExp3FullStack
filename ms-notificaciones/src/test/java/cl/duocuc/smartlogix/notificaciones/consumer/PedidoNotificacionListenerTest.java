package cl.duocuc.smartlogix.notificaciones.consumer;

import cl.duocuc.smartlogix.notificaciones.dto.NotificacionRequestDTO;
import cl.duocuc.smartlogix.notificaciones.event.PedidoEvent;
import cl.duocuc.smartlogix.notificaciones.service.NotificacionService;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoNotificacionListenerTest {

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private Channel channel;

    @InjectMocks
    private PedidoNotificacionListener listener;

    private PedidoEvent evento() {
        return PedidoEvent.builder()
                .pedidoId(1L)
                .skuProducto("SKU-001")
                .cantidad(2)
                .clienteEmail("cliente@test.cl")
                .estado("CREADO")
                .tipoEvento("PEDIDO_CREADO")
                .build();
    }

    @Test
    void eventoValido_creaYEnviaNotificacion_yHaceAck() throws IOException {
        assertDoesNotThrow(() -> listener.recibirEventoPedido(evento(), channel, 1L));
        verify(notificacionService, times(1)).crearYEnviar(any(NotificacionRequestDTO.class));
        verify(channel, times(1)).basicAck(1L, false);
    }

    @Test
    void falloEnProcesamiento_rechazaSinReencolar_paraIrALaDlq() throws IOException {
        doThrow(new RuntimeException("fallo simulado"))
                .when(notificacionService).crearYEnviar(any(NotificacionRequestDTO.class));

        assertDoesNotThrow(() -> listener.recibirEventoPedido(evento(), channel, 1L));
        verify(channel, times(1)).basicNack(1L, false, false);
    }
}
