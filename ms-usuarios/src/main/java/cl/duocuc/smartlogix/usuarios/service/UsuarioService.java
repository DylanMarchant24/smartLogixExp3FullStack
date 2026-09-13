package cl.duocuc.smartlogix.usuarios.service;
import cl.duocuc.smartlogix.usuarios.models.Usuario;
import cl.duocuc.smartlogix.usuarios.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class UsuarioService {
 private final UsuarioRepository repo;
 public UsuarioService(UsuarioRepository repo){this.repo=repo;}
 public List<Usuario> listar(){return repo.findByActivoTrueOrderByIdDesc();}
 public Optional<Usuario> porId(Long id){return repo.findById(id);}
 public Optional<Usuario> porEmail(String email){return repo.findByEmailIgnoreCase(email);}
 public Usuario crear(Usuario u){return repo.save(u);}
 public Usuario registrarLogin(String email){
  Usuario u=repo.findByEmailIgnoreCase(email).orElseThrow(()->new NoSuchElementException("Usuario no encontrado"));
  if(!Boolean.TRUE.equals(u.getActivo())) throw new IllegalStateException("Usuario inactivo");
  u.setUltimoLogin(LocalDateTime.now()); return repo.save(u);
 }
 public boolean desactivar(Long id){return repo.findById(id).map(u->{u.setActivo(false);repo.save(u);return true;}).orElse(false);}
}