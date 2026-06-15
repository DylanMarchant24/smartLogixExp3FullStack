package cl.duocuc.smartlogix.bff.controller;

import cl.duocuc.smartlogix.bff.dto.DashboardDTO;
import cl.duocuc.smartlogix.bff.service.BffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador del BFF - expone endpoints unificados al frontend React.
 * Actua como Backend For Frontend y API Gateway interno.
 *
 * Puerto: 8080 -> unico punto de entrada desde el frontend.
 */
@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "*")
public class BffController {

    private final BffService bffService;

    public BffController(BffService bffService) {
        this.bffService = bffService;
    }

    /**
     * GET /api/bff/dashboard
     * Combina datos de inventario, pedidos y envios en una sola respuesta.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> dashboard() {
        return ResponseEntity.ok(bffService.obtenerDashboard());
    }

    /** GET /api/bff/inventario -> proxy hacia ms-inventario */
    @GetMapping("/inventario")
    public ResponseEntity<List<Map<String, Object>>> inventario() {
        return ResponseEntity.ok(bffService.obtenerInventario());
    }

    /** POST /api/bff/inventario -> crea producto en ms-inventario */
    @PostMapping("/inventario")
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bffService.crearProducto(body));
    }

    /** PUT /api/bff/inventario/{id} -> actualiza producto en ms-inventario */
    @PutMapping("/inventario/{id}")
    public ResponseEntity<Map<String, Object>> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bffService.actualizarProducto(id, body));
    }

    /** DELETE /api/bff/inventario/{id} -> elimina producto en ms-inventario */
    @DeleteMapping("/inventario/{id}")
    public ResponseEntity<Map<String, Object>> eliminarProducto(@PathVariable Long id) {
        return ResponseEntity.ok(bffService.eliminarProducto(id));
    }

    /** GET /api/bff/pedidos -> proxy hacia ms-pedidos */
    @GetMapping("/pedidos")
    public ResponseEntity<List<Map<String, Object>>> pedidos() {
        return ResponseEntity.ok(bffService.obtenerPedidos());
    }

    /** POST /api/bff/pedidos -> crea pedido en ms-pedidos */
    @PostMapping("/pedidos")
    public ResponseEntity<Map<String, Object>> crearPedido(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bffService.crearPedido(body));
    }

    /** PATCH /api/bff/pedidos/{id}/estado -> cambia estado del pedido */
    @PatchMapping("/pedidos/{id}/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstadoPedido(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bffService.cambiarEstadoPedido(id, body));
    }

    /** GET /api/bff/envios -> proxy hacia ms-envios */
    @GetMapping("/envios")
    public ResponseEntity<List<Map<String, Object>>> envios() {
        return ResponseEntity.ok(bffService.obtenerEnvios());
    }

    /** POST /api/bff/envios -> crea envio en ms-envios */
    @PostMapping("/envios")
    public ResponseEntity<Map<String, Object>> crearEnvio(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bffService.crearEnvio(body));
    }

    /** PATCH /api/bff/envios/{id}/estado -> actualiza estado del envio */
    @PatchMapping("/envios/{id}/estado")
    public ResponseEntity<Map<String, Object>> actualizarEstadoEnvio(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bffService.actualizarEstadoEnvio(id, body));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleError(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
