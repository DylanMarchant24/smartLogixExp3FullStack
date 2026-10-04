package cl.duocuc.smartlogix.notificaciones.consumer;

import cl.duocuc.smartlogix.notificaciones.config.RabbitMQConfig;
import cl.duocuc.smartlogix.notificaciones.dto.NotificacionRequestDTO;
import cl.duocuc.smartlogix.notificaciones.event.PedidoEvent;
import cl.duocuc.smartlogix.notificaciones.model.TipoNotificacion;
import cl.duocuc.smartlogix.notificaciones.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor RabbitMQ de eventos de pedidos en ms-notificaciones.
 * Escucha la cola 'smartlogix.notificaciones.queue' vinculada al TopicExchange con routing key 'pedido.*'.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoNotificacionListener {

    private final NotificacionService notificacionService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NOTIFICACIONES)
    public void recibirEventoPedido(PedidoEvent event) {
        log.info("[RabbitMQ Consumer - ms-notificaciones] Evento recibido en cola '{}': {}",
                RabbitMQConfig.QUEUE_NOTIFICACIONES, event);

        try {
            String destinatario = (event.getClienteEmail() != null && !event.getClienteEmail().isBlank())
                    ? event.getClienteEmail()
                    : "cliente@smartlogix.cl";

            String asunto = "SmartLogix - Actualización de Pedido #" + event.getPedidoId();
            String mensaje = String.format(
                    "Estimado/a cliente, le informamos que su pedido #%d (%s, cantidad: %d) ha cambiado a estado: %s. %s",
                    event.getPedidoId() != null ? event.getPedidoId() : 0,
                    event.getSkuProducto() != null ? event.getSkuProducto() : "N/A",
                    event.getCantidad() != null ? event.getCantidad() : 1,
                    event.getEstado() != null ? event.getEstado() : "PROCESANDO",
                    event.getMensaje() != null ? event.getMensaje() : ""
            );

            NotificacionRequestDTO request = new NotificacionRequestDTO(
                    destinatario,
                    TipoNotificacion.EMAIL,
                    asunto,
                    mensaje
            );

            notificacionService.crearYEnviar(request);
            log.info("[RabbitMQ Consumer - ms-notificaciones] Notificación registrada y enviada exitosamente para pedido #{}", event.getPedidoId());
        } catch (Exception e) {
            log.error("[RabbitMQ Consumer - ms-notificaciones] Error al procesar notificación de evento: {}", e.getMessage(), e);
        }
    }
}
