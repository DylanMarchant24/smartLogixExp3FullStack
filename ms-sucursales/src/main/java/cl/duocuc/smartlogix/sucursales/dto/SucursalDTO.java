package cl.duocuc.smartlogix.sucursales.dto;
import cl.duocuc.smartlogix.sucursales.model.TipoSucursal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SucursalDTO {
    private Long id;
    @NotBlank(message="El código es obligatorio") private String codigo;
    @NotBlank(message="El nombre es obligatorio") private String nombre;
    @NotBlank(message="La dirección es obligatoria") private String direccion;
    @NotBlank(message="La comuna es obligatoria") private String comuna;
    @NotBlank(message="La ciudad es obligatoria") private String ciudad;
    private String telefono;
    private String horarioAtencion;
    @NotNull(message="El tipo de sucursal es obligatorio") private TipoSucursal tipo;
    private Boolean activo;
}
