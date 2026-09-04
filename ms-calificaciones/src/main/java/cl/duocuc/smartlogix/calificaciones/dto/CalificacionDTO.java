package cl.duocuc.smartlogix.calificaciones.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionDTO {

    private Long id;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String clienteNombre;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima permitida es 1")
    @Max(value = 5, message = "La puntuación máxima permitida es 5")
    private Integer puntuacion;

    @Size(max = 255, message = "El comentario no puede superar los 255 caracteres")
    private String comentario;

    private LocalDateTime fechaCreacion;
}
