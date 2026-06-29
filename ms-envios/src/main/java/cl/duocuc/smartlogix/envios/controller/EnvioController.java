package cl.duocuc.smartlogix.envios.controller;

import cl.duocuc.smartlogix.envios.dto.EnvioDTO;
import cl.duocuc.smartlogix.envios.model.EstadoEnvio;
import cl.duocuc.smartlogix.envios.service.EnvioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/envios")
@CrossOrigin(origins = "*")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public ResponseEntity<List<EnvioDTO>> listar() {
        return ResponseEntity.ok(envioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvioDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.obtenerPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<EnvioDTO>> listarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(envioService.listarPorPedido(pedidoId));
    }

    @GetMapping("/seguimiento/{codigo}")
    public ResponseEntity<EnvioDTO> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(envioService.buscarPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<EnvioDTO> crear(@Valid @RequestBody EnvioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(envioService.crearEnvio(dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String estadoRequest = body.get("estado");

        if (estadoRequest == null || estadoRequest.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El campo estado es obligatorio"));
        }

        try {
            EstadoEnvio estado = EstadoEnvio.valueOf(estadoRequest.trim().toUpperCase());
            return ResponseEntity.ok(envioService.actualizarEstado(id, estado));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Estado de envío inválido: " + estadoRequest));
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleError(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
