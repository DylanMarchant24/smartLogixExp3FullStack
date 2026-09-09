package cl.duocuc.smartlogix.cupones.controller;

import cl.duocuc.smartlogix.cupones.dto.*;
import cl.duocuc.smartlogix.cupones.service.CuponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cupones")
@RequiredArgsConstructor
public class CuponController {

    private final CuponService cuponService;

    @PostMapping
    public ResponseEntity<CuponResponseDTO> crear(@Valid @RequestBody CuponRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuponService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<CuponResponseDTO>> listar() {
        return ResponseEntity.ok(cuponService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuponResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cuponService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<CuponResponseDTO> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(cuponService.desactivar(id));
    }

    @PostMapping("/validar")
    public ResponseEntity<ValidacionCuponResponseDTO> validar(@Valid @RequestBody ValidacionCuponRequestDTO request) {
        return ResponseEntity.ok(cuponService.validar(request));
    }
}
