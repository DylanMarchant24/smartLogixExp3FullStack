package cl.duocuc.smartlogix.rabbitadmin.controller;

import cl.duocuc.smartlogix.rabbitadmin.dto.ConsumerGroupInfoDTO;
import cl.duocuc.smartlogix.rabbitadmin.dto.CreateKafkaTopicRequest;
import cl.duocuc.smartlogix.rabbitadmin.dto.KafkaTopicInfoDTO;
import cl.duocuc.smartlogix.rabbitadmin.service.KafkaAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kafka/admin")
public class KafkaAdminController {

    private final KafkaAdminService kafkaAdminService;

    public KafkaAdminController(KafkaAdminService kafkaAdminService) {
        this.kafkaAdminService = kafkaAdminService;
    }

    @PostMapping("/topics")
    public ResponseEntity<String> crearTopico(@Valid @RequestBody CreateKafkaTopicRequest request) {
        try {
            kafkaAdminService.crearTopico(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Tópico '" + request.getNombre() + "' creado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear tópico Kafka: " + e.getMessage());
        }
    }

    @GetMapping("/topics")
    public ResponseEntity<List<String>> listarTopicos() {
        try {
            return ResponseEntity.ok(kafkaAdminService.listarTopicos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/topics/{nombre}")
    public ResponseEntity<KafkaTopicInfoDTO> obtenerDetalleTopico(@PathVariable String nombre) {
        try {
            return ResponseEntity.ok(kafkaAdminService.obtenerDetalleTopico(nombre));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/topics/{nombre}")
    public ResponseEntity<String> eliminarTopico(@PathVariable String nombre) {
        try {
            kafkaAdminService.eliminarTopico(nombre);
            return ResponseEntity.ok("Tópico '" + nombre + "' eliminado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar tópico: " + e.getMessage());
        }
    }

    @GetMapping("/consumer-groups")
    public ResponseEntity<List<ConsumerGroupInfoDTO>> listarGruposConsumidores() {
        try {
            return ResponseEntity.ok(kafkaAdminService.listarGruposConsumidores());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/consumer-groups/{groupId}")
    public ResponseEntity<String> eliminarGrupoConsumidor(@PathVariable String groupId) {
        try {
            kafkaAdminService.eliminarGrupoConsumidor(groupId);
            return ResponseEntity.ok("Grupo de consumidores '" + groupId + "' eliminado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar grupo de consumidores: " + e.getMessage());
        }
    }
}