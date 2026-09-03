package cl.duocuc.smartlogix.calificaciones.controller;

import cl.duocuc.smartlogix.calificaciones.dto.CalificacionDTO;
import cl.duocuc.smartlogix.calificaciones.dto.PromedioCalificacionDTO;
import cl.duocuc.smartlogix.calificaciones.service.CalificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calificaciones")
@CrossOrigin(origins = "*")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    /**
     * POST /api/calificaciones → Registra una nueva calificación
     */
    @PostMapping
    public ResponseEntity<CalificacionDTO> crear(@Valid @RequestBody CalificacionDTO dto) {
        CalificacionDTO creada = calificacionService.crearCalificacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * GET /api/calificaciones → Retorna todas las calificaciones registradas
     */
    @GetMapping
    public ResponseEntity<List<CalificacionDTO>> obtenerTodas() {
        return ResponseEntity.ok(calificacionService.obtenerTodas());
    }

    /**
     * GET /api/calificaciones/producto/{productoId} → Retorna las calificaciones de un producto
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<CalificacionDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(calificacionService.obtenerPorProducto(productoId));
    }

    /**
     * GET /api/calificaciones/producto/{productoId}/promedio → Retorna el promedio y total de reseñas de un producto
     */
    @GetMapping("/producto/{productoId}/promedio")
    public ResponseEntity<PromedioCalificacionDTO> obtenerPromedioPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(calificacionService.obtenerPromedioPorProducto(productoId));
    }

    // ── Manejadores de Excepciones ───────────────────────────────────────────

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno en el servidor: " + ex.getMessage()));
    }
}
