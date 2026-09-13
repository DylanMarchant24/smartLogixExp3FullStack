package cl.duocuc.smartlogix.sucursales.controller;
import cl.duocuc.smartlogix.sucursales.dto.CambiarEstadoDTO;
import cl.duocuc.smartlogix.sucursales.dto.SucursalDTO;
import cl.duocuc.smartlogix.sucursales.model.TipoSucursal;
import cl.duocuc.smartlogix.sucursales.service.SucursalService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/sucursales")
public class SucursalController {
    private final SucursalService service;
    public SucursalController(SucursalService service) { this.service = service; }
    @GetMapping public ResponseEntity<List<SucursalDTO>> listarTodas(){ return ResponseEntity.ok(service.obtenerTodas()); }
    @GetMapping("/activas") public ResponseEntity<List<SucursalDTO>> listarActivas(@RequestParam(required=false) String comuna, @RequestParam(required=false) TipoSucursal tipo){
        if (comuna != null && !comuna.isBlank()) return ResponseEntity.ok(service.obtenerActivasPorComuna(comuna));
        if (tipo != null) return ResponseEntity.ok(service.obtenerActivasPorTipo(tipo));
        return ResponseEntity.ok(service.obtenerActivas());
    }
    @GetMapping("/{id}") public ResponseEntity<SucursalDTO> detalle(@PathVariable Long id){ return ResponseEntity.ok(service.obtenerPorId(id)); }
    @PostMapping public ResponseEntity<SucursalDTO> crear(@Valid @RequestBody SucursalDTO dto){ return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto)); }
    @PutMapping("/{id}") public ResponseEntity<SucursalDTO> actualizar(@PathVariable Long id, @Valid @RequestBody SucursalDTO dto){ return ResponseEntity.ok(service.actualizar(id,dto)); }
    @PatchMapping("/{id}/estado") public ResponseEntity<SucursalDTO> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoDTO dto){ return ResponseEntity.ok(service.cambiarEstado(id,dto.activo())); }
    @ExceptionHandler(RuntimeException.class) public ResponseEntity<Map<String,String>> error(Exception ex){ return ResponseEntity.badRequest().body(Map.of("error",ex.getMessage())); }
}
