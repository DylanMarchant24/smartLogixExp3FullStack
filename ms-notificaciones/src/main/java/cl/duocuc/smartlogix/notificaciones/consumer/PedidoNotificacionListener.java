package cl.duocuc.smartlogix.notificaciones.consumer;

import cl.duocuc.smartlogix.notificaciones.dto.NotificacionRequestDTO;
import cl.duocuc.smartlogix.notificaciones.event.PedidoEvent;
import cl.duocuc.smartlogix.notificaciones.model.TipoNotificacion;
import cl.duocuc.smartlogix.notificaciones.service.NotificacionService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor RabbitMQ de eventos de pedidos en ms-notificaciones.
 * Escucha la cola centralizada configurada en properties.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoNotificacionListener {

    private final NotificacionService notificacionService;

    // 1. Usamos la variable inyectada del properties (eliminamos la referencia estática)
    @RabbitListener(queues = "${mensajeria.colas.notificaciones}")
    public void recibirEventoPedido(
            PedidoEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        log.info("[RabbitMQ Consumer - ms-notificaciones] Evento recibido: {}", event);

        try {
            // Mantenemos la lógica de negocio intacta que hizo Dylan
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

            // 2. Confirmación manual (ACK) indicando que todo salió bien
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[RabbitMQ Consumer - ms-notificaciones] Error al procesar notificación de evento: {}", e.getMessage(), e);

            // 3. Rechazo manual (NACK) con requeue=false para mandarlo a la DLQ (Dead Letter Queue)
            channel.basicNack(deliveryTag, false, false);
        }
    }
}