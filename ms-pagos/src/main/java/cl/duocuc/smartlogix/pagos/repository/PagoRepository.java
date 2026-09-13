package cl.duocuc.smartlogix.pagos.repository;
import cl.duocuc.smartlogix.pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PagoRepository extends JpaRepository<Pago,Long> { List<Pago> findByPedidoId(Long pedidoId); }
