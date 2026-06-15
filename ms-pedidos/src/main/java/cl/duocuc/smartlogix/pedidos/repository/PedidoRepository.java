package cl.duocuc.smartlogix.pedidos.repository;

import cl.duocuc.smartlogix.pedidos.model.EstadoPedido;
import cl.duocuc.smartlogix.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PATRÓN: Repository Pattern
 * Abstrae el acceso a datos de la lógica de negocio.
 * Spring Data JPA genera la implementación en tiempo de ejecución.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteEmail(String email);
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findBySkuProducto(String skuProducto);
}
