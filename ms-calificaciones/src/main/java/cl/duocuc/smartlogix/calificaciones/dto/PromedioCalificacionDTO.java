package cl.duocuc.smartlogix.calificaciones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromedioCalificacionDTO {

    private Long productoId;
    private Double promedio;

    @JsonProperty("totalReseñas")
    private Integer totalReseñas;

    // Alias sin tilde para compatibilidad con serializadores estándar
    public Integer getTotalResenas() {
        return totalReseñas;
    }

    public void setTotalResenas(Integer totalResenas) {
        this.totalReseñas = totalResenas;
    }
}
