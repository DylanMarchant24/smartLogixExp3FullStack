package cl.duocuc.smartlogix.notificaciones.controller;

import cl.duocuc.smartlogix.notificaciones.dto.NotificacionRequestDTO;
import cl.duocuc.smartlogix.notificaciones.dto.NotificacionResponseDTO;
import cl.duocuc.smartlogix.notificaciones.model.EstadoNotificacion;
import cl.duocuc.smartlogix.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(@Valid @RequestBody NotificacionRequestDTO request) {
        NotificacionResponseDTO creada = notificacionService.crearYEnviar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listarHistorial() {
        return ResponseEntity.ok(notificacionService.obtenerHistorialCompleto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @GetMapping("/destinatario/{destinatario}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorDestinatario(@PathVariable String destinatario) {
        return ResponseEntity.ok(notificacionService.obtenerPorDestinatario(destinatario));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorEstado(@PathVariable EstadoNotificacion estado) {
        return ResponseEntity.ok(notificacionService.obtenerPorEstado(estado));
    }

    @PostMapping("/{id}/reenviar")
    public ResponseEntity<NotificacionResponseDTO> reenviar(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.reenviar(id));
    }
}
