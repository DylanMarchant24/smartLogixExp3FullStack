package cl.duocuc.smartlogix.usuarios.models;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="usuarios")
public class Usuario {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String nombre;
 @Column(nullable=false,unique=true) private String email;
 @Column(nullable=false) private String password;
 @Column(nullable=false) private String rol="USER";
 @Column(nullable=false) private Boolean activo=true;
 @Column(name="fecha_creacion",nullable=false) private LocalDateTime fechaCreacion;
 @Column(name="ultimo_login") private LocalDateTime ultimoLogin;
 @PrePersist void prePersist(){ if(fechaCreacion==null) fechaCreacion=LocalDateTime.now(); if(rol==null) rol="USER"; if(activo==null) activo=true; }
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getPassword(){return password;} public void setPassword(String v){password=v;}
 public String getRol(){return rol;} public void setRol(String v){rol=v;}
 public Boolean getActivo(){return activo;} public void setActivo(Boolean v){activo=v;}
 public LocalDateTime getFechaCreacion(){return fechaCreacion;} public void setFechaCreacion(LocalDateTime v){fechaCreacion=v;}
 public LocalDateTime getUltimoLogin(){return ultimoLogin;} public void setUltimoLogin(LocalDateTime v){ultimoLogin=v;}
}