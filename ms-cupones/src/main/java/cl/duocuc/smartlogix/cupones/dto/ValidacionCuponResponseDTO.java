package cl.duocuc.smartlogix.cupones.dto;

import cl.duocuc.smartlogix.cupones.model.TipoDescuento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionCuponResponseDTO {

    private String codigo;
    private boolean valido;
    private String motivoRechazo;
    private TipoDescuento tipo;
    private BigDecimal montoCompra;
    private BigDecimal montoRebaja;
    private BigDecimal montoFinal;
}
