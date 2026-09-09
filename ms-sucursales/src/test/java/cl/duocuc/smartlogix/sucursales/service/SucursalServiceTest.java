package cl.duocuc.smartlogix.sucursales.service;
import cl.duocuc.smartlogix.sucursales.dto.SucursalDTO;
import cl.duocuc.smartlogix.sucursales.model.*;
import cl.duocuc.smartlogix.sucursales.repository.SucursalRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class SucursalServiceTest {
    @Mock SucursalRepository repository; @InjectMocks SucursalService service;
    @BeforeEach void init(){ MockitoAnnotations.openMocks(this); }
    private SucursalDTO dto(){ return SucursalDTO.builder().codigo("SUC-100").nombre("Sucursal Test").direccion("Calle 1").comuna("Santiago").ciudad("Santiago").tipo(TipoSucursal.PUNTO_RETIRO).build(); }
    @Test void crearActivaPorDefecto(){ when(repository.findByCodigoIgnoreCase("SUC-100")).thenReturn(Optional.empty()); when(repository.save(any())).thenAnswer(i->{ Sucursal s=i.getArgument(0); s.setId(1L); return s; }); assertTrue(service.crear(dto()).getActivo()); }
    @Test void rechazaCodigoDuplicado(){ when(repository.findByCodigoIgnoreCase("SUC-100")).thenReturn(Optional.of(new Sucursal())); assertThrows(IllegalArgumentException.class,()->service.crear(dto())); }
    @Test void cambiarEstado(){ Sucursal s=Sucursal.builder().id(1L).codigo("SUC-1").nombre("X").direccion("D").comuna("C").ciudad("S").tipo(TipoSucursal.TIENDA_FISICA).activo(true).build(); when(repository.findById(1L)).thenReturn(Optional.of(s)); when(repository.save(any())).thenAnswer(i->i.getArgument(0)); assertFalse(service.cambiarEstado(1L,false).getActivo()); }
}
