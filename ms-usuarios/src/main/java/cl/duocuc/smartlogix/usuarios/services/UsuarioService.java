package main.java.cl.duocuc.smartlogix.usuarios.services;

import cl.duocuc.smartlogix.usuarios.model.Usuario;
import cl.duocuc.smartlogix.usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencias (Spring conecta el repositorio automáticamente)
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crearUsuario(Usuario usuario) {
        // Aquí más adelante podríamos encriptar la contraseña antes de guardar
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public boolean eliminarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(false); // Borrado lógico
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }
}