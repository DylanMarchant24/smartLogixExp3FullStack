package ${package}.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenericDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
}
