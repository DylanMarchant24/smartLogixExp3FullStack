package cl.duocuc.smartlogix.proveedores.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorDTO {

    private Long id;

    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String categoria;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico es inválido")
    private String email;

    private String telefono;

    private String direccion;

    private Boolean activo;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;
}
