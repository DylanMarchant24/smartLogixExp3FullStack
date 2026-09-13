package cl.duocuc.smartlogix.sucursales.model;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="sucursales") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Sucursal {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=20) private String codigo;
    @Column(nullable=false, length=100) private String nombre;
    @Column(nullable=false, length=150) private String direccion;
    @Column(nullable=false, length=50) private String comuna;
    @Column(nullable=false, length=50) private String ciudad;
    @Column(length=20) private String telefono;
    @Column(length=100) private String horarioAtencion;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TipoSucursal tipo;
    @Column(nullable=false) private Boolean activo;
}
