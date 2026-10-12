package cl.duocuc.smartlogix.notificaciones.consumer;

import cl.duocuc.smartlogix.notificaciones.dto.NotificacionRequestDTO;
import cl.duocuc.smartlogix.notificaciones.event.PedidoEvent;
import cl.duocuc.smartlogix.notificaciones.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoNotificacionListenerTest {

    @Mock
    private NotificacionService notificacionService;

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
    void eventoValido_creaYEnviaNotificacion_sinLanzarExcepcion() {
        assertDoesNotThrow(() -> listener.recibirEventoPedido(evento()));
        verify(notificacionService, times(1)).crearYEnviar(any(NotificacionRequestDTO.class));
    }

    @Test
    void falloEnProcesamiento_rechazaSinReencolar_paraIrALaDlq() {
        doThrow(new RuntimeException("fallo simulado"))
                .when(notificacionService).crearYEnviar(any(NotificacionRequestDTO.class));

        assertThrows(AmqpRejectAndDontRequeueException.class,
                () -> listener.recibirEventoPedido(evento()));
    }
}
