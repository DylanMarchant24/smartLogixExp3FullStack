package cl.duocuc.smartlogix.cupones.dto;

import cl.duocuc.smartlogix.cupones.model.TipoDescuento;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuponRequestDTO {

    @NotBlank(message = "El codigo del cupon es obligatorio")
    private String codigo;

    @NotNull(message = "El tipo de descuento es obligatorio (PORCENTAJE o MONTO_FIJO)")
    private TipoDescuento tipo;

    @NotNull(message = "El valor del descuento es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El valor debe ser mayor a 0")
    private BigDecimal valor;

    @NotNull(message = "La fecha de inicio de vigencia es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin de vigencia es obligatoria")
    private LocalDate fechaFin;
}
