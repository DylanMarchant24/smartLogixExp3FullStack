package cl.duocuc.smartlogix.proveedores.controller;

import cl.duocuc.smartlogix.proveedores.dto.ProveedorDTO;
import cl.duocuc.smartlogix.proveedores.service.ProveedorService;
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
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    /**
     * GET /api/proveedores → Lista todos los proveedores
     */
    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> listarTodos() {
        return ResponseEntity.ok(proveedorService.listarTodos());
    }

    /**
     * GET /api/proveedores/activos → Lista solo los proveedores activos
     */
    @GetMapping("/activos")
    public ResponseEntity<List<ProveedorDTO>> listarActivos() {
        return ResponseEntity.ok(proveedorService.listarActivos());
    }

    /**
     * GET /api/proveedores/{id} → Obtiene el detalle de un proveedor por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    /**
     * POST /api/proveedores → Registra un nuevo proveedor
     */
    @PostMapping
    public ResponseEntity<ProveedorDTO> crear(@Valid @RequestBody ProveedorDTO dto) {
        ProveedorDTO creado = proveedorService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * PUT /api/proveedores/{id} → Actualiza un proveedor existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorDTO dto) {
        return ResponseEntity.ok(proveedorService.actualizar(id, dto));
    }

    /**
     * PATCH /api/proveedores/{id}/desactivar → Realiza el borrado lógico del proveedor
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ProveedorDTO> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.desactivar(id));
    }

    // ── Manejadores de Excepciones ───────────────────────────────────────────

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
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
}
