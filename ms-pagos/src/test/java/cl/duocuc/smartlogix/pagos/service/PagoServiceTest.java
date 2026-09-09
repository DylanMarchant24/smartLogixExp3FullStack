package cl.duocuc.smartlogix.pagos.service;
import cl.duocuc.smartlogix.pagos.dto.*;
import cl.duocuc.smartlogix.pagos.model.*;
import cl.duocuc.smartlogix.pagos.repository.PagoRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class PagoServiceTest {
    @Mock PagoRepository repository; @InjectMocks PagoService service;
    @BeforeEach void init(){ MockitoAnnotations.openMocks(this); when(repository.save(any())).thenAnswer(i->{ Pago p=i.getArgument(0); p.setId(1L); return p; }); }
    @Test void apruebaTarjetaDePrueba(){ PagoResponse r=service.procesar(new ProcesarPagoRequest(1L,new BigDecimal("10000"),"TEST-APROBADA","4242")); assertEquals(EstadoPago.APROBADO,r.estado()); }
    @Test void rechazaTarjetaDePrueba(){ PagoResponse r=service.procesar(new ProcesarPagoRequest(1L,new BigDecimal("10000"),"TEST-RECHAZADA","0002")); assertEquals(EstadoPago.RECHAZADO,r.estado()); }
    @Test void rechazaTokenDesconocido(){ PagoResponse r=service.procesar(new ProcesarPagoRequest(1L,new BigDecimal("10000"),"OTRO","1111")); assertEquals(EstadoPago.RECHAZADO,r.estado()); }
}
