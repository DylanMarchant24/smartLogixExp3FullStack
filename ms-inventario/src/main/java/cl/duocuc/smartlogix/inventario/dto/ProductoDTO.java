package cl.duocuc.smartlogix.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El SKU es obligatorio")
    private String sku;

    @NotNull
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull
    @Min(value = 0, message = "El precio no puede ser negativo")
    private BigDecimal precio;

    private String descripcion;
    private String fechaCreacion;
    private String fechaActualizacion;
}
