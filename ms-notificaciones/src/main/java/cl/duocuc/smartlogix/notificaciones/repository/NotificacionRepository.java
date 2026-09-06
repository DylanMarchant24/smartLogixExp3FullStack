package cl.duocuc.smartlogix.notificaciones.repository;

import cl.duocuc.smartlogix.notificaciones.model.EstadoNotificacion;
import cl.duocuc.smartlogix.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByDestinatarioOrderByFechaCreacionDesc(String destinatario);

    List<Notificacion> findByEstadoOrderByFechaCreacionDesc(EstadoNotificacion estado);

    List<Notificacion> findAllByOrderByFechaCreacionDesc();
}
