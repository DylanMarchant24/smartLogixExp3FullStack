package cl.duocuc.smartlogix.proveedores.repository;

import cl.duocuc.smartlogix.proveedores.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PATRÓN: Repository Pattern
 * Abstracción de acceso a datos para la entidad Proveedor.
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    /**
     * Busca un proveedor por su RUT único.
     */
    Optional<Proveedor> findByRut(String rut);

    /**
     * Comprueba si ya existe un proveedor registrado con el RUT especificado.
     */
    boolean existsByRut(String rut);

    /**
     * Obtiene el listado de proveedores que se encuentran en estado activo.
     */
    List<Proveedor> findByActivoTrue();
}
