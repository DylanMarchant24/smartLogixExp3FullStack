package cl.duocuc.smartlogix.envios.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO de evento de negocio para el segundo tópico de Apache Kafka ('smartlogix.envios.tracking').
 * Representa la creación y cambios de estado de despacho/seguimiento de envíos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioTrackingEvent implements Serializable {

    private Long envioId;
    private Long pedidoId;
    private String codigoSeguimiento;
    private String transportista;
    private String direccionDestino;
    private String estado;
    private String tipoEvento;
    private String fechaHora;
    private String mensaje;
}

