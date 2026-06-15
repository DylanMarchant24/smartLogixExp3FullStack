package ${package}.controller;

import ${package}.dto.SummaryDTO;
import ${package}.service.BffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "*")
public class BffController {
    private final BffService bffService;

    public BffController(BffService bffService) {
        this.bffService = bffService;
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryDTO> summary() {
        return ResponseEntity.ok(bffService.getSummary());
    }
}
