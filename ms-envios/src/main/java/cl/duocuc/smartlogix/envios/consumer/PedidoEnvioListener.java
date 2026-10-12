package cl.duocuc.smartlogix.envios.consumer;

import cl.duocuc.smartlogix.envios.config.RabbitMQConfig;
import cl.duocuc.smartlogix.envios.dto.EnvioDTO;
import cl.duocuc.smartlogix.envios.event.PedidoEvent;
import cl.duocuc.smartlogix.envios.service.EnvioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor RabbitMQ en ms-envios.
 * Escucha la cola 'smartlogix.envios.queue' que recibe eventos 'pedido.creado'
 * desde el TopicExchange 'smartlogix.exchange' para generar automáticamente la orden de despacho.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoEnvioListener {

    private final EnvioService envioService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ENVIOS)
    public void procesarPedidoCreado(PedidoEvent event) {
        log.info("[RabbitMQ Consumer - ms-envios] Evento recibido en cola '{}': {}",
                RabbitMQConfig.QUEUE_ENVIOS, event);

        try {
            if (event.getPedidoId() == null) {
                log.warn("[RabbitMQ Consumer - ms-envios] Evento ignorado: pedidoId es nulo");
                return;
            }

            // Evitar duplicar orden de envío si ya existe para este pedido
            var enviosExistentes = envioService.listarPorPedido(event.getPedidoId());
            if (!enviosExistentes.isEmpty()) {
                log.info("[RabbitMQ Consumer - ms-envios] Ya existe envío registrado para el pedido #{}", event.getPedidoId());
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
        } catch (Exception e) {
            log.error("[RabbitMQ Consumer - ms-envios] Error al generar envío automático para pedido: {}", e.getMessage(), e);
        }
    }
}
