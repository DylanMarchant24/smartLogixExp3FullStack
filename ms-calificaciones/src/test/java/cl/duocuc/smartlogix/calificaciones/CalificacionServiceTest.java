package cl.duocuc.smartlogix.calificaciones;

import cl.duocuc.smartlogix.calificaciones.dto.CalificacionDTO;
import cl.duocuc.smartlogix.calificaciones.dto.PromedioCalificacionDTO;
import cl.duocuc.smartlogix.calificaciones.model.Calificacion;
import cl.duocuc.smartlogix.calificaciones.repository.CalificacionRepository;
import cl.duocuc.smartlogix.calificaciones.service.CalificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CalificacionService – Pruebas Unitarias")
class CalificacionServiceTest {

    @Mock
    private CalificacionRepository calificacionRepository;

    @InjectMocks
    private CalificacionService calificacionService;

    private Calificacion calificacionBase;
    private CalificacionDTO calificacionDTOBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        calificacionBase = Calificacion.builder()
                .id(1L)
                .productoId(100L)
                .clienteNombre("Dylan Marchant")
                .puntuacion(5)
                .comentario("Excelente calidad y entrega rápida.")
                .fechaCreacion(LocalDateTime.now())
                .build();

        calificacionDTOBase = CalificacionDTO.builder()
                .productoId(100L)
                .clienteNombre("Dylan Marchant")
                .puntuacion(5)
                .comentario("Excelente calidad y entrega rápida.")
                .build();
    }

    // ── Crear Calificación ───────────────────────────────────────────────────

    @Test
    @DisplayName("crearCalificacion: guarda y retorna la calificación exitosamente")
    void crearCalificacion_datosValidos_retornaDTO() {
        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(calificacionBase);

        CalificacionDTO resultado = calificacionService.crearCalificacion(calificacionDTOBase);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(100L, resultado.getProductoId());
        assertEquals("Dylan Marchant", resultado.getClienteNombre());
        assertEquals(5, resultado.getPuntuacion());
        verify(calificacionRepository, times(1)).save(any(Calificacion.class));
    }

    @Test
    @DisplayName("crearCalificacion: lanza excepción cuando la puntuación es menor a 1")
    void crearCalificacion_puntuacionMenorAUno_lanzaExcepcion() {
        calificacionDTOBase.setPuntuacion(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                calificacionService.crearCalificacion(calificacionDTOBase));

        assertTrue(ex.getMessage().contains("entre 1 y 5"));
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearCalificacion: lanza excepción cuando la puntuación es mayor a 5")
    void crearCalificacion_puntuacionMayorACinco_lanzaExcepcion() {
        calificacionDTOBase.setPuntuacion(6);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                calificacionService.crearCalificacion(calificacionDTOBase));

        assertTrue(ex.getMessage().contains("entre 1 y 5"));
        verify(calificacionRepository, never()).save(any());
    }

    // ── Obtener por Producto ─────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorProducto: devuelve lista de calificaciones para el producto indicado")
    void obtenerPorProducto_retornaListaDTO() {
        when(calificacionRepository.findByProductoId(100L)).thenReturn(List.of(calificacionBase));

        List<CalificacionDTO> lista = calificacionService.obtenerPorProducto(100L);

        assertEquals(1, lista.size());
        assertEquals("Dylan Marchant", lista.get(0).getClienteNombre());
        verify(calificacionRepository, times(1)).findByProductoId(100L);
    }

    // ── Cálculo de Promedios ─────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPromedioPorProducto: calcula promedio y total de reseñas correctamente")
    void obtenerPromedioPorProducto_conCalificaciones_calculaPromedioCorrecto() {
        when(calificacionRepository.calcularPromedioPorProductoId(100L)).thenReturn(4.666666);
        when(calificacionRepository.countByProductoId(100L)).thenReturn(3L);

        PromedioCalificacionDTO promedioDTO = calificacionService.obtenerPromedioPorProducto(100L);

        assertNotNull(promedioDTO);
        assertEquals(100L, promedioDTO.getProductoId());
        assertEquals(4.67, promedioDTO.getPromedio());
        assertEquals(3, promedioDTO.getTotalReseñas());
        verify(calificacionRepository, times(1)).calcularPromedioPorProductoId(100L);
        verify(calificacionRepository, times(1)).countByProductoId(100L);
    }

    @Test
    @DisplayName("obtenerPromedioPorProducto: devuelve 0.0 y total 0 cuando no hay calificaciones")
    void obtenerPromedioPorProducto_sinCalificaciones_retornaCero() {
        when(calificacionRepository.calcularPromedioPorProductoId(200L)).thenReturn(null);
        when(calificacionRepository.countByProductoId(200L)).thenReturn(0L);

        PromedioCalificacionDTO promedioDTO = calificacionService.obtenerPromedioPorProducto(200L);

        assertNotNull(promedioDTO);
        assertEquals(200L, promedioDTO.getProductoId());
        assertEquals(0.0, promedioDTO.getPromedio());
        assertEquals(0, promedioDTO.getTotalReseñas());
    }
}
