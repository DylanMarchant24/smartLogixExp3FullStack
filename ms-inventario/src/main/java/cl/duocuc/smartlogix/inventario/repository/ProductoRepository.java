package cl.duocuc.smartlogix.inventario.repository;

import cl.duocuc.smartlogix.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PATRÓN: Repository Pattern
 * Desacopla la lógica de negocio del acceso a datos.
 * Spring Data JPA genera automáticamente la implementación en tiempo de ejecución.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findBySku(String sku);

    boolean existsBySku(String sku);

    /**
     * Reduce el stock de forma atómica mediante una query JPQL.
     * Evita race conditions al actualizar directamente en BD.
     */
    @Modifying
    @Query("UPDATE Producto p SET p.stock = p.stock - :cantidad WHERE p.sku = :sku AND p.stock >= :cantidad")
    int reducirStock(@Param("sku") String sku, @Param("cantidad") int cantidad);
}
