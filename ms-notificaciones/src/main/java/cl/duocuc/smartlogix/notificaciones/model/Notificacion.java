package cl.duocuc.smartlogix.notificaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String destinatario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoNotificacion tipo;

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false, length = 1000)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaEnvio;

    @Column(length = 500)
    private String detalleError;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoNotificacion.PENDIENTE;
        }
    }
}
