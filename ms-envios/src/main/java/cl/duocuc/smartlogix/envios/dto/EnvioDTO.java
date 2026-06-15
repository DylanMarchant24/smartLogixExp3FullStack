package cl.duocuc.smartlogix.envios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnvioDTO {
    private Long id;

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotBlank(message = "El transportista es obligatorio")
    private String transportista;

    @NotBlank(message = "La dirección de destino es obligatoria")
    private String direccionDestino;

    private String estado;
    private String fechaCreacion;
    private String fechaEntrega;
    private String codigoSeguimiento;
}
