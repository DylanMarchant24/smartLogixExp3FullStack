package cl.duocuc.smartlogix.notificaciones.dto;

import cl.duocuc.smartlogix.notificaciones.model.EstadoNotificacion;
import cl.duocuc.smartlogix.notificaciones.model.Notificacion;
import cl.duocuc.smartlogix.notificaciones.model.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponseDTO {

    private Long id;
    private String destinatario;
    private TipoNotificacion tipo;
    private String asunto;
    private String mensaje;
    private EstadoNotificacion estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEnvio;
    private String detalleError;

    public static NotificacionResponseDTO fromEntity(Notificacion n) {
        return new NotificacionResponseDTO(
                n.getId(),
                n.getDestinatario(),
                n.getTipo(),
                n.getAsunto(),
                n.getMensaje(),
                n.getEstado(),
                n.getFechaCreacion(),
                n.getFechaEnvio(),
                n.getDetalleError()
        );
    }
}
