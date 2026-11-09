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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor RabbitMQ de eventos de pedidos en ms-notificaciones.
 * Escucha la cola definida en application.yml y mapea el payload directamente al DTO PedidoEvent.
 * Utiliza acuse de recibo manual (basicAck / basicNack) para enviar fallos persistentes a la DLQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoNotificacionListener {

    private final NotificacionService notificacionService;

    @Value("${mensajeria.exchange.dlx:smartlogix.dlx}")
    private String dlxName = "smartlogix.dlx";

    @Value("${mensajeria.colas.dlq-notificaciones:smartlogix.notificaciones.dlq}")
    private String dlqName = "smartlogix.notificaciones.dlq";

    @RabbitListener(queues = "${mensajeria.colas.notificaciones}")
    public void recibirEventoPedido(
            @Payload PedidoEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        log.info("[RabbitMQ Consumer - ms-notificaciones] Evento recibido: {}", event);

        try {
            if (event == null || event.getPedidoId() == null) {
                throw new IllegalArgumentException("El evento de pedido recibido es inválido o no contiene pedidoId");
            }

            String destinatario = (event.getClienteEmail() != null && !event.getClienteEmail().isBlank())
                    ? event.getClienteEmail()
                    : "cliente@smartlogix.cl";

            String asunto = "SmartLogix - Actualización de Pedido #" + event.getPedidoId();
            String mensaje = String.format(
                    "Estimado/a cliente, le informamos que su pedido #%d (%s, cantidad: %d) ha cambiado a estado: %s. %s",
                    event.getPedidoId(),
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

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[RabbitMQ Consumer - ms-notificaciones] Error al procesar notificación de evento: {}", e.getMessage(), e);
            log.warn("[RabbitMQ DLQ - ms-notificaciones] Mensaje rechazado sin reencolar (basicNack requeue=false). Se enviará al DLX '{}' -> DLQ '{}'. Evento: {}",
                    dlxName, dlqName, event);

            channel.basicNack(deliveryTag, false, false);
        }
    }
}
