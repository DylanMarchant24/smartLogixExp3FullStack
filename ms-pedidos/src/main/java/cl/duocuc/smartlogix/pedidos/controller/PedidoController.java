package cl.duocuc.smartlogix.pedidos.controller;

import cl.duocuc.smartlogix.pedidos.dto.PedidoDTO;
import cl.duocuc.smartlogix.pedidos.model.EstadoPedido;
import cl.duocuc.smartlogix.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listar() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pedidoService.listarPorEstado(EstadoPedido.valueOf(estado.toUpperCase())));
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> crear(@Valid @RequestBody PedidoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crearPedido(dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        EstadoPedido estado = EstadoPedido.valueOf(body.get("estado").toUpperCase());
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleError(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
