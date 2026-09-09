package cl.duocuc.smartlogix.sucursales.repository;
import cl.duocuc.smartlogix.sucursales.model.Sucursal;
import cl.duocuc.smartlogix.sucursales.model.TipoSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    Optional<Sucursal> findByCodigoIgnoreCase(String codigo);
    List<Sucursal> findByActivoTrue();
    List<Sucursal> findByComunaIgnoreCaseAndActivoTrue(String comuna);
    List<Sucursal> findByTipoAndActivoTrue(TipoSucursal tipo);
}
