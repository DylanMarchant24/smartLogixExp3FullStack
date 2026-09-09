package cl.duocuc.smartlogix.notificaciones.service;

import cl.duocuc.smartlogix.notificaciones.dto.NotificacionRequestDTO;
import cl.duocuc.smartlogix.notificaciones.dto.NotificacionResponseDTO;
import cl.duocuc.smartlogix.notificaciones.model.EstadoNotificacion;
import cl.duocuc.smartlogix.notificaciones.model.Notificacion;
import cl.duocuc.smartlogix.notificaciones.repository.NotificacionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final EmailSenderService emailSenderService;

    @Transactional
    public NotificacionResponseDTO crearYEnviar(NotificacionRequestDTO request) {
        Notificacion notificacion = Notificacion.builder()
                .destinatario(request.getDestinatario())
                .tipo(request.getTipo())
                .asunto(request.getAsunto())
                .mensaje(request.getMensaje())
                .estado(EstadoNotificacion.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();

        notificacion = notificacionRepository.save(notificacion);
        return procesarEnvio(notificacion);
    }

    @Transactional
    public NotificacionResponseDTO reenviar(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notificacion no encontrada con id " + id));
        return procesarEnvio(notificacion);
    }

    private NotificacionResponseDTO procesarEnvio(Notificacion notificacion) {
        EmailSenderService.EnvioResultado resultado = emailSenderService.enviar(
                notificacion.getDestinatario(),
                notificacion.getAsunto(),
                notificacion.getMensaje()
        );

        if (resultado.exitoso()) {
            notificacion.setEstado(EstadoNotificacion.ENVIADO);
            notificacion.setFechaEnvio(LocalDateTime.now());
            notificacion.setDetalleError(null);
        } else {
            notificacion.setEstado(EstadoNotificacion.FALLIDO);
            notificacion.setDetalleError(resultado.detalleError());
        }

        notificacion = notificacionRepository.save(notificacion);
        return NotificacionResponseDTO.fromEntity(notificacion);
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> obtenerHistorialCompleto() {
        return notificacionRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(NotificacionResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> obtenerPorDestinatario(String destinatario) {
        return notificacionRepository.findByDestinatarioOrderByFechaCreacionDesc(destinatario)
                .stream()
                .map(NotificacionResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> obtenerPorEstado(EstadoNotificacion estado) {
        return notificacionRepository.findByEstadoOrderByFechaCreacionDesc(estado)
                .stream()
                .map(NotificacionResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificacionResponseDTO obtenerPorId(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notificacion no encontrada con id " + id));
        return NotificacionResponseDTO.fromEntity(notificacion);
    }
}
