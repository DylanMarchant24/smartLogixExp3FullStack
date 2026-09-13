package cl.duocuc.smartlogix.calificaciones.repository;

import cl.duocuc.smartlogix.calificaciones.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PATRÓN: Repository Pattern
 * Abstracción de acceso a datos para la entidad Calificacion.
 */
@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    /**
     * Obtiene todas las calificaciones asociadas a un producto específico.
     */
    List<Calificacion> findByProductoId(Long productoId);

    /**
     * Calcula el promedio aritmético de las puntuaciones de un producto dado.
     * Retorna null si no hay registros asociados.
     */
    @Query("SELECT AVG(c.puntuacion) FROM Calificacion c WHERE c.productoId = :productoId")
    Double calcularPromedioPorProductoId(@Param("productoId") Long productoId);

    /**
     * Cuenta el total de calificaciones registradas para un producto.
     */
    long countByProductoId(Long productoId);
}
