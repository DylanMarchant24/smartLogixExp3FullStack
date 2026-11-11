package cl.duocuc.smartlogix.envios.service;

import cl.duocuc.smartlogix.envios.dto.EnvioDTO;
import cl.duocuc.smartlogix.envios.event.EnvioTrackingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Productor de eventos Apache Kafka para ms-envios.
 * Publica eventos tipados con EnvioTrackingEvent hacia el tópico 'smartlogix.envios.tracking'.
 */
@Slf4j
@Service
public class EnvioKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${mensajeria.kafka.topics.envios-tracking:smartlogix.envios.tracking}")
    private String topicEnviosTracking = "smartlogix.envios.tracking";

    @Autowired
    public EnvioKafkaPublisher(@Autowired(required = false) KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEnvioCreado(EnvioDTO envio) {
        publicarEvento(envio, "ENVIO_CREADO",
                "Envío generado con código " + envio.getCodigoSeguimiento() + " para pedido #" + envio.getPedidoId());
    }

    public void publicarEstadoActualizado(EnvioDTO envio) {
        publicarEvento(envio, "ENVIO_ESTADO_ACTUALIZADO",
                "Envío " + envio.getCodigoSeguimiento() + " actualizado a estado " + envio.getEstado());
    }

    private void publicarEvento(EnvioDTO envio, String tipoEvento, String mensaje) {
        if (kafkaTemplate == null || envio == null) {
            return;
        }
        EnvioTrackingEvent event = EnvioTrackingEvent.builder()
                .envioId(envio.getId())
                .pedidoId(envio.getPedidoId())
                .codigoSeguimiento(envio.getCodigoSeguimiento())
                .transportista(envio.getTransportista())
                .direccionDestino(envio.getDireccionDestino())
                .estado(envio.getEstado())
                .tipoEvento(tipoEvento)
                .fechaHora(LocalDateTime.now().toString())
                .mensaje(mensaje)
                .build();

        try {
            String key = envio.getCodigoSeguimiento() != null
                    ? envio.getCodigoSeguimiento()
                    : String.valueOf(envio.getId());
            log.info("[Kafka Producer - ms-envios] Publicando evento '{}' en tópico '{}' con key='{}'",
                    tipoEvento, topicEnviosTracking, key);
            kafkaTemplate.send(topicEnviosTracking, key, event);
        } catch (Exception e) {
            log.error("[Kafka Producer - ms-envios] Error al publicar evento de tracking en Kafka: {}", e.getMessage(), e);
        }
    }
}

