package cl.duocuc.smartlogix.envios.repository;

import cl.duocuc.smartlogix.envios.model.Envio;
import cl.duocuc.smartlogix.envios.model.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PATRÓN: Repository Pattern
 * Abstrae el acceso a datos del microservicio de envíos.
 */
@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    List<Envio> findByPedidoId(Long pedidoId);
    List<Envio> findByEstado(EstadoEnvio estado);
    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);
}
