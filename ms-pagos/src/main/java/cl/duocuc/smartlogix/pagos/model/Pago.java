package cl.duocuc.smartlogix.pagos.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="pagos") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Pago {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private Long pedidoId;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal monto;
    @Column(nullable=false, length=40) private String tarjetaToken;
    @Column(nullable=false, length=4) private String ultimos4;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private EstadoPago estado;
    @Column(nullable=false, unique=true, length=40) private String codigoTransaccion;
    @Column(nullable=false) private LocalDateTime fecha;
    private String motivo;
}
