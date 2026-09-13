package cl.duocuc.smartlogix.usuarios.controller;
import cl.duocuc.smartlogix.usuarios.models.Usuario;
import cl.duocuc.smartlogix.usuarios.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/usuarios")
public class UsuarioController {
 private final UsuarioService service;
 public UsuarioController(UsuarioService service){this.service=service;}
 @GetMapping public List<UsuarioResponse> listar(){return service.listar().stream().map(UsuarioResponse::of).toList();}
 @GetMapping("/{id}") public ResponseEntity<UsuarioResponse> porId(@PathVariable Long id){return service.porId(id).map(UsuarioResponse::of).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
 @GetMapping("/email/{email}") public ResponseEntity<UsuarioResponse> porEmail(@PathVariable String email){return service.porEmail(email).map(UsuarioResponse::of).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
 @PostMapping public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody Usuario u){return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.of(service.crear(u)));}
 @PostMapping("/registrar-login") public UsuarioResponse registrarLogin(@RequestBody Map<String,String> body){return UsuarioResponse.of(service.registrarLogin(body.get("email")));}
 @DeleteMapping("/{id}") public ResponseEntity<Void> desactivar(@PathVariable Long id){return service.desactivar(id)?ResponseEntity.noContent().build():ResponseEntity.notFound().build();}
 public record UsuarioResponse(Long id,String nombre,String email,String rol,Boolean activo,java.time.LocalDateTime fechaCreacion,java.time.LocalDateTime ultimoLogin){
  static UsuarioResponse of(Usuario u){return new UsuarioResponse(u.getId(),u.getNombre(),u.getEmail(),u.getRol(),u.getActivo(),u.getFechaCreacion(),u.getUltimoLogin());}
 }
}