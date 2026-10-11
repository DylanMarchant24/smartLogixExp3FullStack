package cl.duocuc.smartlogix.envios.consumer;

import cl.duocuc.smartlogix.envios.dto.EnvioDTO;
import cl.duocuc.smartlogix.envios.event.PedidoEvent;
import cl.duocuc.smartlogix.envios.service.EnvioService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor RabbitMQ en ms-envios.
 * Escucha la cola centralizada desde el properties para generar la orden de despacho.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoEnvioListener {

    private final EnvioService envioService;

    // 1. Usamos la variable inyectada del properties para cumplir con la pauta
    @RabbitListener(queues = "${mensajeria.colas.envios}")
    public void procesarPedidoCreado(
            PedidoEvent event, 
            Channel channel, 
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        
        log.info("[RabbitMQ Consumer - ms-envios] Evento recibido: {}", event);

        try {
            if (event.getPedidoId() == null) {
                log.warn("[RabbitMQ Consumer - ms-envios] Evento ignorado: pedidoId es nulo");
                // Confirmamos el mensaje (ACK) para sacarlo de la cola, ya que no nos sirve
                channel.basicAck(deliveryTag, false);
                return;
            }

            // Evitar duplicar orden de envío si ya existe para este pedido
            var enviosExistentes = envioService.listarPorPedido(event.getPedidoId());
            if (!enviosExistentes.isEmpty()) {
                log.info("[RabbitMQ Consumer - ms-envios] Ya existe envío registrado para el pedido #{}", event.getPedidoId());
                // Confirmamos el mensaje (ACK) porque el flujo terminó correctamente
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

            // 2. Confirmación manual (ACK) indicando éxito total en la lógica de negocio
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[RabbitMQ Consumer - ms-envios] Error al generar envío automático para pedido: {}", e.getMessage(), e);
            
            // 3. Rechazo manual (NACK) con requeue=false para mandarlo a la DLQ.
            // Esto es vital para que Benjamín pueda hacer su parte.
            channel.basicNack(deliveryTag, false, false);
        }
    }
}