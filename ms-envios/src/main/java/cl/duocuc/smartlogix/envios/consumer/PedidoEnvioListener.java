package cl.duocuc.smartlogix.envios.consumer;

import cl.duocuc.smartlogix.envios.dto.EnvioDTO;
import cl.duocuc.smartlogix.envios.event.PedidoEvent;
import cl.duocuc.smartlogix.envios.service.EnvioService;
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
 * Consumidor RabbitMQ en ms-envios.
 * Escucha la cola centralizada en application.yml y mapea directamente el DTO PedidoEvent.
 * Opera con acuse de recibo manual (basicAck / basicNack) para enviar fallos a la DLQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoEnvioListener {

    private final EnvioService envioService;

    @Value("${mensajeria.exchange.dlx:smartlogix.dlx}")
    private String dlxName = "smartlogix.dlx";

    @Value("${mensajeria.colas.dlq-envios:smartlogix.envios.dlq}")
    private String dlqEnvios = "smartlogix.envios.dlq";

    @RabbitListener(queues = "${mensajeria.colas.envios}")
    public void procesarPedidoCreado(
            @Payload PedidoEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        log.info("[RabbitMQ Consumer - ms-envios] Evento recibido: {}", event);

        try {
            if (event == null || event.getPedidoId() == null) {
                throw new IllegalArgumentException("Evento de pedido inválido: pedidoId es nulo");
            }

            // Evitar duplicar orden de envío si ya existe para este pedido
            var enviosExistentes = envioService.listarPorPedido(event.getPedidoId());
            if (!enviosExistentes.isEmpty()) {
                log.info("[RabbitMQ Consumer - ms-envios] Ya existe envío registrado para el pedido #{}", event.getPedidoId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            EnvioDTO nuevoEnvio = new EnvioDTO();
            nuevoEnvio.setPedidoId(event.getPedidoId());
            nuevoEnvio.setTransportista("SmartLogix Express");
            String destinatario = (event.getClienteEmail() != null && !event.getClienteEmail().isBlank())
                    ? event.getClienteEmail()
                    : "cliente";
            nuevoEnvio.setDireccionDestino("Despacho eCommerce - Cliente: " + destinatario);

            EnvioDTO creado = envioService.crearEnvio(nuevoEnvio);
            log.info("[RabbitMQ Consumer - ms-envios] Envío automático creado con éxito. Tracking: {}, Pedido: {}",
                    creado.getCodigoSeguimiento(), creado.getPedidoId());

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[RabbitMQ Consumer - ms-envios] Error al generar envío automático para pedido: {}", e.getMessage(), e);
            log.warn("[RabbitMQ DLQ - ms-envios] Mensaje rechazado sin reencolar (basicNack requeue=false). Se enviará al DLX '{}' -> DLQ '{}'. Evento: {}",
                    dlxName, dlqEnvios, event);

            channel.basicNack(deliveryTag, false, false);
        }
    }
}
