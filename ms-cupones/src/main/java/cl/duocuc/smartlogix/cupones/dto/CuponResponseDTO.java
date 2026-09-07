package cl.duocuc.smartlogix.cupones.dto;

import cl.duocuc.smartlogix.cupones.model.Cupon;
import cl.duocuc.smartlogix.cupones.model.TipoDescuento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuponResponseDTO {

    private Long id;
    private String codigo;
    private TipoDescuento tipo;
    private BigDecimal valor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;

    public static CuponResponseDTO fromEntity(Cupon c) {
        return new CuponResponseDTO(
                c.getId(), c.getCodigo(), c.getTipo(), c.getValor(),
                c.getFechaInicio(), c.getFechaFin(), c.getActivo()
        );
    }
}
