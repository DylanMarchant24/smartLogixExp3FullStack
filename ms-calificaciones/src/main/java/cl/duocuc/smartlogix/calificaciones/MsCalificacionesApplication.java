package cl.duocuc.smartlogix.calificaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsCalificacionesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCalificacionesApplication.class, args);
    }
}
