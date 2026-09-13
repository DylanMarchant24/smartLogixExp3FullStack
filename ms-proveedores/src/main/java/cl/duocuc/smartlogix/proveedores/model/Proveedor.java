package cl.duocuc.smartlogix.proveedores.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad Proveedor – PATRÓN: Repository Pattern (JPA Entity)
 * Mapea las empresas proveedoras asociadas a la gestión de inventario y logística.
 */
@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El RUT es obligatorio")
    @Column(nullable = false, unique = true)
    private String rut;

    @NotBlank(message = "La razón social es obligatoria")
    @Column(nullable = false)
    private String razonSocial;

    @Column(length = 100)
    private String rubro;

    @NotBlank(message = "El email de contacto es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Column(nullable = false)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
