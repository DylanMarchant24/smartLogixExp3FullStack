package cl.duocuc.smartlogix.auditoria.controller;

import cl.duocuc.smartlogix.auditoria.model.Auditoria;
import cl.duocuc.smartlogix.auditoria.service.AuditoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService service;

    public AuditoriaController(AuditoriaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Auditoria>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @PostMapping
    public ResponseEntity<Auditoria> crear(@RequestBody Auditoria auditoria) {
        Auditoria nueva = service.guardarAuditoria(auditoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }
}