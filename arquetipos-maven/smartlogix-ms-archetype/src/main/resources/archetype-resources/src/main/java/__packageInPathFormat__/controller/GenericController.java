package ${package}.controller;

import ${package}.dto.GenericDTO;
import ${package}.service.GenericService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class GenericController {
    private final GenericService service;

    public GenericController(GenericService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<GenericDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<GenericDTO> crear(@Valid @RequestBody GenericDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }
}
