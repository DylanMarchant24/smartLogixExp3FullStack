package cl.duocuc.smartlogix.inventario.controller;

import cl.duocuc.smartlogix.inventario.dto.ProductoDTO;
import cl.duocuc.smartlogix.inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    /** GET /api/inventario → lista todos los productos */
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar() {
        return ResponseEntity.ok(inventarioService.listarTodos());
    }

    /** GET /api/inventario/{id} → obtiene producto por ID */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    /** GET /api/inventario/sku/{sku} → obtiene producto por SKU (usado por ms-pedidos vía Circuit Breaker) */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductoDTO> obtenerPorSku(@PathVariable String sku) {
        return ResponseEntity.ok(inventarioService.obtenerPorSku(sku));
    }

    /** POST /api/inventario → crea un nuevo producto */
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.crear(dto));
    }

    /** PUT /api/inventario/{id} → actualiza nombre, stock y precio */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(inventarioService.actualizar(id, dto));
    }

    /**
     * PATCH /api/inventario/sku/{sku}/reducir
     * Endpoint consumido por ms-pedidos para descontar stock tras aprobar un pedido.
     * Body: { "cantidad": N }
     */
    @PatchMapping("/sku/{sku}/reducir")
    public ResponseEntity<ProductoDTO> reducirStock(
            @PathVariable String sku,
            @RequestBody Map<String, Integer> body) {
        int cantidad = body.getOrDefault("cantidad", 0);
        if (cantidad <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(inventarioService.reducirStock(sku, cantidad));
    }

    /** DELETE /api/inventario/{id} → elimina un producto */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Manejador global de errores de negocio para este controlador */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleError(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }
}
