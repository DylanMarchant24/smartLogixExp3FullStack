package cl.duocuc.smartlogix.auditoria.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditorias")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accion;

    @Column(nullable = false)
    private String entidad;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    @Column(name = "usuario_responsable", nullable = false)
    private String usuarioResponsable;

    @Column(name = "fecha_accion")
    private LocalDateTime fechaAccion = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String detalles;

    public Auditoria() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }

    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }

    public String getUsuarioResponsable() { return usuarioResponsable; }
    public void setUsuarioResponsable(String usuarioResponsable) { this.usuarioResponsable = usuarioResponsable; }

    public LocalDateTime getFechaAccion() { return fechaAccion; }
    public void setFechaAccion(LocalDateTime fechaAccion) { this.fechaAccion = fechaAccion; }

    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }
}