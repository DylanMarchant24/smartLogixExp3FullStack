package cl.duocuc.smartlogix.usuarios.repositories;
import cl.duocuc.smartlogix.usuarios.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
 Optional<Usuario> findByEmailIgnoreCase(String email);
 List<Usuario> findByActivoTrueOrderByIdDesc();
}