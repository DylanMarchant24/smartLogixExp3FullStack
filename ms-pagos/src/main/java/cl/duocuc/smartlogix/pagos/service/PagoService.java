package cl.duocuc.smartlogix.pagos.service;
import cl.duocuc.smartlogix.pagos.dto.*;
import cl.duocuc.smartlogix.pagos.model.*;
import cl.duocuc.smartlogix.pagos.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class PagoService {
    public static final String TOKEN_APROBADO = "TEST-APROBADA";
    public static final String TOKEN_RECHAZADO = "TEST-RECHAZADA";
    private final PagoRepository repository;
    public PagoService(PagoRepository repository){ this.repository=repository; }
    @Transactional public PagoResponse procesar(ProcesarPagoRequest req){
        boolean aprobado = TOKEN_APROBADO.equalsIgnoreCase(req.tarjetaToken());
        String motivo = aprobado ? "Pago simulado aprobado" : (TOKEN_RECHAZADO.equalsIgnoreCase(req.tarjetaToken()) ? "Pago rechazado por tarjeta de prueba" : "Token de tarjeta no autorizado para pruebas");
        Pago pago = Pago.builder().pedidoId(req.pedidoId()).monto(req.monto()).tarjetaToken(req.tarjetaToken()).ultimos4(req.ultimos4()).estado(aprobado?EstadoPago.APROBADO:EstadoPago.RECHAZADO).codigoTransaccion("PAY-"+UUID.randomUUID().toString().substring(0,8).toUpperCase()).fecha(LocalDateTime.now()).motivo(motivo).build();
        return toResponse(repository.save(pago));
    }
    public List<PagoResponse> listar(){ return repository.findAll().stream().map(this::toResponse).toList(); }
    public List<PagoResponse> porPedido(Long pedidoId){ return repository.findByPedidoId(pedidoId).stream().map(this::toResponse).toList(); }
    private PagoResponse toResponse(Pago p){ return new PagoResponse(p.getId(),p.getPedidoId(),p.getMonto(),p.getEstado(),p.getCodigoTransaccion(),p.getFecha().toString(),p.getMotivo()); }
}
