package cl.duocuc.smartlogix.cupones.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionCuponRequestDTO {

    @NotBlank(message = "El codigo del cupon es obligatorio")
    private String codigo;

    @NotNull(message = "El monto de la compra es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto de la compra debe ser mayor a 0")
    private BigDecimal montoCompra;
}
