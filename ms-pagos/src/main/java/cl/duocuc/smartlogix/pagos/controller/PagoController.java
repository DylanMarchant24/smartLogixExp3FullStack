package cl.duocuc.smartlogix.pagos.controller;
import cl.duocuc.smartlogix.pagos.dto.*;
import cl.duocuc.smartlogix.pagos.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/pagos")
public class PagoController {
    private final PagoService service;
    public PagoController(PagoService service){ this.service=service; }
    @PostMapping("/procesar") public ResponseEntity<PagoResponse> procesar(@Valid @RequestBody ProcesarPagoRequest req){ return ResponseEntity.status(HttpStatus.CREATED).body(service.procesar(req)); }
    @GetMapping public ResponseEntity<List<PagoResponse>> listar(){ return ResponseEntity.ok(service.listar()); }
    @GetMapping("/pedido/{pedidoId}") public ResponseEntity<List<PagoResponse>> porPedido(@PathVariable Long pedidoId){ return ResponseEntity.ok(service.porPedido(pedidoId)); }
}
