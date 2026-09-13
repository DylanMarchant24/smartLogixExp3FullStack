package cl.duocuc.smartlogix.cupones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cupones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDescuento tipo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    public boolean estaVigente(LocalDate fecha) {
        return Boolean.TRUE.equals(activo)
                && !fecha.isBefore(fechaInicio)
                && !fecha.isAfter(fechaFin);
    }
}
