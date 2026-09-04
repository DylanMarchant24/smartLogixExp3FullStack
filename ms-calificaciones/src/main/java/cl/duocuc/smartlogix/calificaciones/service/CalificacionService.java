package cl.duocuc.smartlogix.calificaciones.service;

import cl.duocuc.smartlogix.calificaciones.dto.CalificacionDTO;
import cl.duocuc.smartlogix.calificaciones.dto.PromedioCalificacionDTO;
import cl.duocuc.smartlogix.calificaciones.model.Calificacion;
import cl.duocuc.smartlogix.calificaciones.repository.CalificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Capa de Servicio para la gestión de Calificaciones y Reseñas.
 * PATRÓN: Repository Pattern (la lógica de negocio se separa del acceso a datos).
 */
@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;

    public CalificacionService(CalificacionRepository calificacionRepository) {
        this.calificacionRepository = calificacionRepository;
    }

    /**
     * Registra una nueva calificación para un producto.
     */
    @Transactional
    public CalificacionDTO crearCalificacion(CalificacionDTO dto) {
        if (dto.getPuntuacion() == null || dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuación debe estar comprendida entre 1 y 5");
        }
        if (dto.getProductoId() == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio");
        }
        if (dto.getClienteNombre() == null || dto.getClienteNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío");
        }

        Calificacion entidad = toEntity(dto);
        Calificacion guardada = calificacionRepository.save(entidad);
        return toDTO(guardada);
    }

    /**
     * Obtiene el listado completo de calificaciones registradas.
     */
    @Transactional(readOnly = true)
    public List<CalificacionDTO> obtenerTodas() {
        return calificacionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las calificaciones asociadas a un producto determinado.
     */
    @Transactional(readOnly = true)
    public List<CalificacionDTO> obtenerPorProducto(Long productoId) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        return calificacionRepository.findByProductoId(productoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el resumen de métricas (promedio y total de reseñas) para un producto.
     */
    @Transactional(readOnly = true)
    public PromedioCalificacionDTO obtenerPromedioPorProducto(Long productoId) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }

        Double promedioRaw = calificacionRepository.calcularPromedioPorProductoId(productoId);
        long total = calificacionRepository.countByProductoId(productoId);

        // Si no existen calificaciones, retornamos promedio 0.0 y total 0
        double promedioFinal = (promedioRaw != null) ? Math.round(promedioRaw * 100.0) / 100.0 : 0.0;

        return PromedioCalificacionDTO.builder()
                .productoId(productoId)
                .promedio(promedioFinal)
                .totalReseñas((int) total)
                .build();
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    public CalificacionDTO toDTO(Calificacion entity) {
        return CalificacionDTO.builder()
                .id(entity.getId())
                .productoId(entity.getProductoId())
                .clienteNombre(entity.getClienteNombre())
                .puntuacion(entity.getPuntuacion())
                .comentario(entity.getComentario())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }

    public Calificacion toEntity(CalificacionDTO dto) {
        return Calificacion.builder()
                .productoId(dto.getProductoId())
                .clienteNombre(dto.getClienteNombre())
                .puntuacion(dto.getPuntuacion())
                .comentario(dto.getComentario())
                .build();
    }
}
