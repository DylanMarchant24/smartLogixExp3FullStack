package cl.duocuc.smartlogix.usuarios.service;

import cl.duocuc.smartlogix.usuarios.model.Usuario;
import cl.duocuc.smartlogix.usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UsuarioService {
    
    private final UsuarioRepository repo;
    // Instanciado directamente para evitar errores de Beans faltantes
    private final RestTemplate restTemplate = new RestTemplate(); 

    public UsuarioService(UsuarioRepository repo){
        this.repo=repo;
    }

    public List<Usuario> listar(){
        return repo.findByActivoTrueOrderByIdDesc();
    }

    public Optional<Usuario> porId(Long id){
        return repo.findById(id);
    }

    public Optional<Usuario> porEmail(String email){
        return repo.findByEmailIgnoreCase(email);
    }

    public Usuario crear(Usuario u){
        Usuario nuevoUsuario = repo.save(u);
        
        // --- INICIO TRAZABILIDAD (CREAR) ---
        try {
            String auditoriaUrl = "http://localhost:8082/api/auditoria"; 
            Map<String, Object> logJson = new HashMap<>();
            logJson.put("accion", "CREACION_USUARIO"); 
            logJson.put("entidad", "Usuario"); 
            logJson.put("entidadId", nuevoUsuario.getId()); 
            logJson.put("usuarioResponsable", nuevoUsuario.getEmail() != null ? nuevoUsuario.getEmail() : "Sistema"); 
            logJson.put("detalles", "Se creó un nuevo usuario con correo: " + nuevoUsuario.getEmail());
            
            restTemplate.postForObject(auditoriaUrl, logJson, Map.class);
            System.out.println("Log de auditoría enviado con éxito.");
        } catch (Exception e) {
            System.err.println("No se pudo enviar el log a ms-auditoria: " + e.getMessage());
        }
        // --- FIN TRAZABILIDAD ---
        
        return nuevoUsuario;
    }

    public Usuario registrarLogin(String email){
        Usuario u = repo.findByEmailIgnoreCase(email)
                .orElseThrow(()->new NoSuchElementException("Usuario no encontrado"));
        if(!Boolean.TRUE.equals(u.getActivo())) throw new IllegalStateException("Usuario inactivo");
        u.setUltimoLogin(LocalDateTime.now()); 
        return repo.save(u);
    }

    public boolean desactivar(Long id){
        return repo.findById(id).map(u -> {
            u.setActivo(false);
            repo.save(u);
            
            // --- INICIO TRAZABILIDAD (DESACTIVAR) ---
            try {
                String auditoriaUrl = "http://localhost:8082/api/auditoria"; 
                Map<String, Object> logJson = new HashMap<>();
                logJson.put("accion", "DESACTIVACION_USUARIO"); 
                logJson.put("entidad", "Usuario"); 
                logJson.put("entidadId", u.getId()); 
                logJson.put("usuarioResponsable", u.getEmail() != null ? u.getEmail() : "Sistema"); 
                logJson.put("detalles", "Se desactivó el usuario con email: " + u.getEmail());
                
                restTemplate.postForObject(auditoriaUrl, logJson, Map.class);
                System.out.println("Log de auditoría por desactivación enviado con éxito.");
            } catch (Exception e) {
                System.err.println("No se pudo enviar el log de desactivación a ms-auditoria: " + e.getMessage());
            }
            // --- FIN TRAZABILIDAD ---
            
            return true;
        }).orElse(false);
    }
}