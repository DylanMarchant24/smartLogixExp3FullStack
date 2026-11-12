package cl.duocuc.smartlogix.rabbitadmin.dto;

public class KafkaTopicInfoDTO {
    private String nombre;
    
    public KafkaTopicInfoDTO(String nombre) {
        this.nombre = nombre;
    }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}