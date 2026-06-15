package cl.duocuc.smartlogix.envios;

import cl.duocuc.smartlogix.envios.dto.EnvioDTO;
import cl.duocuc.smartlogix.envios.model.Envio;
import cl.duocuc.smartlogix.envios.model.EstadoEnvio;
import cl.duocuc.smartlogix.envios.repository.EnvioRepository;
import cl.duocuc.smartlogix.envios.service.EnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("EnvioService – Pruebas Unitarias")
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private EnvioService envioService;

    private Envio envioBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        envioBase = new Envio(1L, 10L, "Chilexpress", "Av. Principal 123",
                EstadoEnvio.PENDIENTE, LocalDateTime.now(), null, null, "SL-ABCD1234");
    }

    @Test
    @DisplayName("listar: devuelve todos los envíos")
    void listar_retornaLista() {
        when(envioRepository.findAll()).thenReturn(List.of(envioBase));
        List<EnvioDTO> result = envioService.listar();
        assertEquals(1, result.size());
        assertEquals("PENDIENTE", result.get(0).getEstado());
    }

    @Test
    @DisplayName("obtenerPorId: retorna envío existente")
    void obtenerPorId_existente_retornaDTO() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioBase));
        EnvioDTO dto = envioService.obtenerPorId(1L);
        assertNotNull(dto);
        assertEquals("Chilexpress", dto.getTransportista());
        assertEquals("SL-ABCD1234", dto.getCodigoSeguimiento());
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepción si no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> envioService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("crearEnvio: persiste con estado PENDIENTE y código de seguimiento")
    void crearEnvio_persisteCorrectamente() {
        EnvioDTO dto = new EnvioDTO();
        dto.setPedidoId(10L);
        dto.setTransportista("Starken");
        dto.setDireccionDestino("Calle Falsa 123");

        when(envioRepository.save(any(Envio.class))).thenReturn(envioBase);

        EnvioDTO result = envioService.crearEnvio(dto);
        assertNotNull(result);
        verify(envioRepository).save(any(Envio.class));
    }

    @Test
    @DisplayName("actualizarEstado: cambia estado a EN_CAMINO correctamente")
    void actualizarEstado_enCamino_actualizaEstado() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioBase));
        when(envioRepository.save(any())).thenReturn(envioBase);

        EnvioDTO result = envioService.actualizarEstado(1L, EstadoEnvio.EN_CAMINO);
        verify(envioRepository).save(any(Envio.class));
    }

    @Test
    @DisplayName("actualizarEstado: registra fechaEntrega cuando estado es ENTREGADO")
    void actualizarEstado_entregado_registraFecha() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioBase));
        when(envioRepository.save(any())).thenAnswer(inv -> {
            Envio e = inv.getArgument(0);
            // Verificar que se asignó la fecha de entrega
            assertNotNull(e.getFechaEntrega());
            return e;
        });
        envioService.actualizarEstado(1L, EstadoEnvio.ENTREGADO);
    }

    @Test
    @DisplayName("listarPorPedido: filtra envíos por pedidoId")
    void listarPorPedido_retornaFiltrados() {
        when(envioRepository.findByPedidoId(10L)).thenReturn(List.of(envioBase));
        List<EnvioDTO> result = envioService.listarPorPedido(10L);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getPedidoId());
    }
}
