package cl.duocuc.smartlogix.pagos.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record ProcesarPagoRequest(
        @NotNull Long pedidoId,
        @NotNull @DecimalMin(value="1.0", message="El monto debe ser mayor a 0") BigDecimal monto,
        @NotBlank String tarjetaToken,
        @Pattern(regexp="\\d{4}", message="ultimos4 debe contener 4 dígitos") String ultimos4) {}
