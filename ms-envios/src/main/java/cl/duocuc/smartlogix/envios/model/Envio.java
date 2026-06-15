package cl.duocuc.smartlogix.envios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad Envío – gestiona el despacho físico de un pedido aprobado.
 * Referencia al pedido mediante pedidoId (clave foránea lógica,
 * sin FK real para mantener el desacoplamiento entre microservicios).
 */
@Entity
@Table(name = "envios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del pedido es obligatorio")
    @Column(nullable = false)
    private Long pedidoId;

    @NotBlank(message = "El transportista es obligatorio")
    @Column(nullable = false)
    private String transportista;

    @NotBlank(message = "La dirección de destino es obligatoria")
    @Column(nullable = false)
    private String direccionDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEnvio estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaEntrega;

    /** Código de seguimiento generado al crear el envío */
    @Column(unique = true)
    private String codigoSeguimiento;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
