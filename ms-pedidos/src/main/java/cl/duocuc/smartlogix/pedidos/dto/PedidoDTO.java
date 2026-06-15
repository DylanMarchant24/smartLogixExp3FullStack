package cl.duocuc.smartlogix.pedidos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PedidoDTO {
    private Long id;

    @NotBlank(message = "El SKU del producto es obligatorio")
    private String skuProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotBlank(message = "El email del cliente es obligatorio")
    @Email(message = "Formato de email inválido")
    private String clienteEmail;

    private String estado;
    private String fechaCreacion;
    private String fechaActualizacion;
    private Integer ultimoStockConocido;
}
