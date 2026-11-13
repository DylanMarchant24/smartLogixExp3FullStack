package cl.duocuc.smartlogix.auditoria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO tipado para deserializar eventos del tópico 'smartlogix.envios.tracking'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioKafkaEventDTO implements Serializable {

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

