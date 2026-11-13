package cl.duocuc.smartlogix.auditoria.consumer;

import cl.duocuc.smartlogix.auditoria.dto.EnvioKafkaEventDTO;
import cl.duocuc.smartlogix.auditoria.dto.PedidoKafkaEventDTO;
import cl.duocuc.smartlogix.auditoria.model.Auditoria;
import cl.duocuc.smartlogix.auditoria.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Consumidor de Apache Kafka para ms-auditoria.
 * Consume ambos tópicos del proyecto ('smartlogix.pedidos.eventos' y 'smartlogix.envios.tracking')
 * mapeando directamente a los DTOs tipados y separando la lógica de dominio en AuditoriaService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditoriaKafkaConsumer {

    private final AuditoriaService auditoriaService;

    @KafkaListener(
            topics = "${mensajeria.kafka.topics.pedidos-eventos:smartlogix.pedidos.eventos}",
            groupId = "${spring.kafka.consumer.group-id:smartlogix-auditoria-group}",
            containerFactory = "pedidoKafkaListenerContainerFactory"
    )
    public void consumirEventoPedido(
            @Payload PedidoKafkaEventDTO event,
            @Header(name = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(name = KafkaHeaders.OFFSET, required = false) Long offset) {

        log.info("[Kafka Consumer - ms-auditoria] Evento de Pedido recibido (partición={}, offset={}): {}",
                partition, offset, event);

        if (event == null || event.getPedidoId() == null) {
            throw new IllegalArgumentException("Evento de pedido inválido en Kafka: pedidoId es obligatorio");
        }

        Auditoria auditoria = new Auditoria();
        auditoria.setAccion(event.getTipoEvento() != null ? event.getTipoEvento() : "PEDIDO_EVENTO");
        auditoria.setEntidad("PEDIDO");
        auditoria.setEntidadId(event.getPedidoId());
        auditoria.setUsuarioResponsable(
                (event.getClienteEmail() != null && !event.getClienteEmail().isBlank())
                        ? event.getClienteEmail()
                        : "sistema-kafka"
        );
        auditoria.setFechaAccion(LocalDateTime.now());
        auditoria.setDetalles(String.format(
                "SKU=%s | Cantidad=%s | Estado=%s | Mensaje=%s | Partition=%s | Offset=%s",
                event.getSkuProducto(),
                event.getCantidad(),
                event.getEstado(),
                event.getMensaje(),
                partition,
                offset
        ));

        auditoriaService.guardarAuditoria(auditoria);
        log.info("[Kafka Consumer - ms-auditoria] Auditoría registrada exitosamente para Pedido #{}", event.getPedidoId());
    }

    @KafkaListener(
            topics = "${mensajeria.kafka.topics.envios-tracking:smartlogix.envios.tracking}",
            groupId = "${spring.kafka.consumer.group-id:smartlogix-auditoria-group}",
            containerFactory = "envioKafkaListenerContainerFactory"
    )
    public void consumirEventoEnvio(
            @Payload EnvioKafkaEventDTO event,
            @Header(name = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(name = KafkaHeaders.OFFSET, required = false) Long offset) {

        log.info("[Kafka Consumer - ms-auditoria] Evento de Envío recibido (partición={}, offset={}): {}",
                partition, offset, event);

        if (event == null || (event.getEnvioId() == null && event.getPedidoId() == null)) {
            throw new IllegalArgumentException("Evento de envío inválido en Kafka: requiere envioId o pedidoId");
        }

        Auditoria auditoria = new Auditoria();
        auditoria.setAccion(event.getTipoEvento() != null ? event.getTipoEvento() : "ENVIO_TRACKING");
        auditoria.setEntidad("ENVIO");
        auditoria.setEntidadId(event.getEnvioId() != null ? event.getEnvioId() : event.getPedidoId());
        auditoria.setUsuarioResponsable(
                (event.getTransportista() != null && !event.getTransportista().isBlank())
                        ? event.getTransportista()
                        : "sistema-kafka"
        );
        auditoria.setFechaAccion(LocalDateTime.now());
        auditoria.setDetalles(String.format(
                "Tracking=%s | PedidoId=%s | Estado=%s | Destino=%s | Mensaje=%s | Partition=%s | Offset=%s",
                event.getCodigoSeguimiento(),
                event.getPedidoId(),
                event.getEstado(),
                event.getDireccionDestino(),
                event.getMensaje(),
                partition,
                offset
        ));

        auditoriaService.guardarAuditoria(auditoria);
        log.info("[Kafka Consumer - ms-auditoria] Auditoría registrada exitosamente para Envío tracking={}",
                event.getCodigoSeguimiento());
    }
}

