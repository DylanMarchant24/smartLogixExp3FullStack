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
 * Todos los microservicios (inventario, pedidos, envios, calificaciones,
 * cupones, notificaciones, pagos, proveedores, sucursales, usuarios)
 * se consumen exclusivamente a traves de este BFF.
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

    // ── Inventario ────────────────────────────────────────────────────────

    @GetMapping("/inventario")
    public ResponseEntity<List<Map<String, Object>>> inventario() {
        return ResponseEntity.ok(bffService.obtenerInventario());
    }

    @PostMapping("/inventario")
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bffService.crearProducto(body));
    }

    @PutMapping("/inventario/{id}")
    public ResponseEntity<Map<String, Object>> actualizarProducto(
            @PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bffService.actualizarProducto(id, body));
    }

    @DeleteMapping("/inventario/{id}")
    public ResponseEntity<Map<String, Object>> eliminarProducto(@PathVariable Long id) {
        return ResponseEntity.ok(bffService.eliminarProducto(id));
    }

    // ── Pedidos ─────────────────────────────────────────────────────────

    @GetMapping("/pedidos")
    public ResponseEntity<List<Map<String, Object>>> pedidos() {
        return ResponseEntity.ok(bffService.obtenerPedidos());
    }

    @PostMapping("/pedidos")
    public ResponseEntity<Map<String, Object>> crearPedido(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bffService.crearPedido(body));
    }

    @PatchMapping("/pedidos/{id}/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstadoPedido(
            @PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bffService.cambiarEstadoPedido(id, body));
    }

    // ── Envios ─────────────────────────────────────────────────────────

    @GetMapping("/envios")
    public ResponseEntity<List<Map<String, Object>>> envios() {
        return ResponseEntity.ok(bffService.obtenerEnvios());
    }

    @PostMapping("/envios")
    public ResponseEntity<Map<String, Object>> crearEnvio(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bffService.crearEnvio(body));
    }

    @PatchMapping("/envios/{id}/estado")
    public ResponseEntity<Map<String, Object>> actualizarEstadoEnvio(
            @PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bffService.actualizarEstadoEnvio(id, body));
    }

    // ── Calificaciones ─────────────────────────────────────────────────

    @GetMapping("/calificaciones")
    public ResponseEntity<List<Map<String, Object>>> calificaciones() {
        return ResponseEntity.ok(bffService.obtenerCalificaciones());
    }

    @GetMapping("/calificaciones/producto/{productoId}")
    public ResponseEntity<List<Map<String, Object>>> calificacionesPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(bffService.obtenerCalificacionesPorProducto(productoId));
    }

    @GetMapping("/calificaciones/producto/{productoId}/promedio")
    public ResponseEntity<Map<String, Object>> promedioCalificacion(@PathVariable Long productoId) {
        return ResponseEntity.ok(bffService.obtenerPromedioCalificacion(productoId));
    }

    @PostMapping("/calificaciones")
    public ResponseEntity<Map<String, Object>> crearCalificacion(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bffService.crearCalificacion(body));
    }

    // ── Cupones ─────────────────────────────────────────────────────────

    @GetMapping("/cupones")
    public ResponseEntity<List<Map<String, Object>>> cupones() {
        return ResponseEntity.ok(bffService.obtenerCupones());
    }

    @GetMapping("/cupones/{id}")
    public ResponseEntity<Map<String, Object>> cuponPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bffService.obtenerCuponPorId(id));
    }

    @PostMapping("/cupones")
    public ResponseEntity<Map<String, Object>> crearCupon(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bffService.crearCupon(body));
    }

    @PatchMapping("/cupones/{id}/desactivar")
    public ResponseEntity<Map<String, Object>> desactivarCupon(@PathVariable Long id) {
        return ResponseEntity.ok(bffService.desactivarCupon(id));
    }

    @PostMapping("/cupones/validar")
    public ResponseEntity<Map<String, Object>> validarCupon(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bffService.validarCupon(body));
    }

    // ── Notificaciones ─────────────────────────────────────────────────

    @GetMapping("/notificaciones")
    public ResponseEntity<List<Map<String, Object>>> notificaciones() {
        return ResponseEntity.ok(bffService.obtenerNotificaciones());
    }

    @GetMapping("/notificaciones/{id}")
    public ResponseEntity<Map<String, Object>> notificacionPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bffService.obtenerNotificacionPorId(id));
    }

    @GetMapping("/notificaciones/destinatario/{destinatario}")
    public ResponseEntity<List<Map<String, Object>>> notificacionesPorDestinatario(@PathVariable String destinatario) {
        return ResponseEntity.ok(bffService.obtenerNotificacionesPorDestinatario(destinatario));
    }

    @GetMapping("/notificaciones/estado/{estado}")
    public ResponseEntity<List<Map<String, Object>>> notificacionesPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(bffService.obtenerNotificacionesPorEstado(estado));
    }

    @PostMapping("/notificaciones")
    public ResponseEntity<Map<String, Object>> crearNotificacion(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bffService.crearNotificacion(body));
    }

    @PostMapping("/notificaciones/{id}/reenviar")
    public ResponseEntity<Map<String, Object>> reenviarNotificacion(@PathVariable Long id) {
        return ResponseEntity.ok(bffService.reenviarNotificacion(id));
    }

    // ── Pagos ─────────────────────────────────────────────────────────

    @GetMapping("/pagos")
    public ResponseEntity<List<Map<String, Object>>> pagos() {
        return ResponseEntity.ok(bffService.obtenerPagos());
    }

    @PostMapping("/pagos/procesar")
    public ResponseEntity<Map<String, Object>> procesarPago(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bffService.procesarPago(body));
    }

    @GetMapping("/pagos/pedido/{pedidoId}")
    public ResponseEntity<List<Map<String, Object>>> pagosPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(bffService.obtenerPagosPorPedido(pedidoId));
    }

    // ── Proveedores ─────────────────────────────────────────────────

    @GetMapping("/proveedores")
    public ResponseEntity<List<Map<String, Object>>> proveedores() {
        return ResponseEntity.ok(bffService.obtenerProveedores());
    }

    @GetMapping("/proveedores/activos")
    public ResponseEntity<List<Map<String, Object>>> proveedoresActivos() {
        return ResponseEntity.ok(bffService.obtenerProveedoresActivos());
    }

    @PostMapping("/proveedores")
    public ResponseEntity<Map<String, Object>> crearProveedor(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bffService.crearProveedor(body));
    }

    @PutMapping("/proveedores/{id}")
    public ResponseEntity<Map<String, Object>> actualizarProveedor(
            @PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bffService.actualizarProveedor(id, body));
    }

    @PatchMapping("/proveedores/{id}/desactivar")
    public ResponseEntity<Map<String, Object>> desactivarProveedor(@PathVariable Long id) {
        return ResponseEntity.ok(bffService.desactivarProveedor(id));
    }

    // ── Sucursales ──────────────────────────────────────────────────

    @GetMapping("/sucursales")
    public ResponseEntity<List<Map<String, Object>>> sucursales() {
        return ResponseEntity.ok(bffService.obtenerSucursales());
    }

    @GetMapping("/sucursales/activas")
    public ResponseEntity<List<Map<String, Object>>> sucursalesActivas() {
        return ResponseEntity.ok(bffService.obtenerSucursalesActivas());
    }

    @PostMapping("/sucursales")
    public ResponseEntity<Map<String, Object>> crearSucursal(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bffService.crearSucursal(body));
    }

    @PutMapping("/sucursales/{id}")
    public ResponseEntity<Map<String, Object>> actualizarSucursal(
            @PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bffService.actualizarSucursal(id, body));
    }

    @PatchMapping("/sucursales/{id}/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstadoSucursal(
            @PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bffService.cambiarEstadoSucursal(id, body));
    }

    // ── Usuarios ───────────────────────────────────────────────────

    @GetMapping("/usuarios")
    public ResponseEntity<List<Map<String, Object>>> usuarios() {
        return ResponseEntity.ok(bffService.obtenerUsuarios());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleError(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
