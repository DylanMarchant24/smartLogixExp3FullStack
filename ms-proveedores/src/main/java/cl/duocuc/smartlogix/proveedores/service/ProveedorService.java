package cl.duocuc.smartlogix.proveedores.service;

import cl.duocuc.smartlogix.proveedores.dto.ProveedorDTO;
import cl.duocuc.smartlogix.proveedores.model.Proveedor;
import cl.duocuc.smartlogix.proveedores.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Capa de Servicio para la administración de proveedores.
 * PATRÓN: Repository Pattern.
 */
@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    /**
     * Registra un nuevo proveedor validando unicidad de RUT.
     */
    @Transactional
    public ProveedorDTO crear(ProveedorDTO dto) {
        if (proveedorRepository.existsByRut(dto.getRut())) {
            throw new IllegalArgumentException("Ya existe un proveedor registrado con el RUT: " + dto.getRut());
        }

        Proveedor entidad = toEntity(dto);
        entidad.setActivo(true);
        Proveedor guardado = proveedorRepository.save(entidad);
        return toDTO(guardado);
    }

    /**
     * Lista todos los proveedores registrados en el sistema.
     */
    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarTodos() {
        return proveedorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista únicamente los proveedores en estado activo.
     */
    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarActivos() {
        return proveedorRepository.findByActivoTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el detalle de un proveedor por su ID.
     */
    @Transactional(readOnly = true)
    public ProveedorDTO obtenerPorId(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
        return toDTO(proveedor);
    }

    /**
     * Actualiza la información de un proveedor existente.
     */
    @Transactional
    public ProveedorDTO actualizar(Long id, ProveedorDTO dto) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

        // Si se modifica el RUT, verificar que no colisione con otro proveedor
        if (dto.getRut() != null && !dto.getRut().equals(existente.getRut())) {
            if (proveedorRepository.existsByRut(dto.getRut())) {
                throw new IllegalArgumentException("Ya existe un proveedor registrado con el RUT: " + dto.getRut());
            }
            existente.setRut(dto.getRut());
        }

        existente.setNombre(dto.getNombre());
        existente.setCategoria(dto.getCategoria());
        existente.setEmail(dto.getEmail());
        existente.setTelefono(dto.getTelefono());
        existente.setDireccion(dto.getDireccion());

        if (dto.getActivo() != null) {
            existente.setActivo(dto.getActivo());
        }

        Proveedor actualizado = proveedorRepository.save(existente);
        return toDTO(actualizado);
    }

    /**
     * Realiza el borrado lógico del proveedor (activo = false).
     */
    @Transactional
    public ProveedorDTO desactivar(Long id) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

        existente.setActivo(false);
        Proveedor guardado = proveedorRepository.save(existente);
        return toDTO(guardado);
    }

    /**
     * Cambia el estado (activo/inactivo) del proveedor a un valor explicito.
     * A diferencia de desactivar(), permite tanto activar como desactivar,
     * usado por el toggle de estado en el frontend.
     */
    @Transactional
    public ProveedorDTO cambiarEstado(Long id, boolean activo) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

        existente.setActivo(activo);
        Proveedor guardado = proveedorRepository.save(existente);
        return toDTO(guardado);
    }

    // ── Mappers ─────────────────────────────────────────────────────────

    public ProveedorDTO toDTO(Proveedor entity) {
        return ProveedorDTO.builder()
                .id(entity.getId())
                .rut(entity.getRut())
                .nombre(entity.getNombre())
                .categoria(entity.getCategoria())
                .email(entity.getEmail())
                .telefono(entity.getTelefono())
                .direccion(entity.getDireccion())
                .activo(entity.getActivo())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    public Proveedor toEntity(ProveedorDTO dto) {
        return Proveedor.builder()
                .rut(dto.getRut())
                .nombre(dto.getNombre())
                .categoria(dto.getCategoria())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
    }
}
