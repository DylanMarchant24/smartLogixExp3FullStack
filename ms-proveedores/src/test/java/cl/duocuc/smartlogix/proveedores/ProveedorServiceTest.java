package cl.duocuc.smartlogix.proveedores;

import cl.duocuc.smartlogix.proveedores.dto.ProveedorDTO;
import cl.duocuc.smartlogix.proveedores.model.Proveedor;
import cl.duocuc.smartlogix.proveedores.repository.ProveedorRepository;
import cl.duocuc.smartlogix.proveedores.service.ProveedorService;
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

@DisplayName("ProveedorService – Pruebas Unitarias")
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedorBase;
    private ProveedorDTO proveedorDTOBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        proveedorBase = Proveedor.builder()
                .id(1L)
                .rut("76.123.456-7")
                .razonSocial("Logística & Embalajes S.A.")
                .rubro("Embalaje")
                .email("contacto@embalajes.cl")
                .telefono("+56911223344")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        proveedorDTOBase = ProveedorDTO.builder()
                .rut("76.123.456-7")
                .razonSocial("Logística & Embalajes S.A.")
                .rubro("Embalaje")
                .email("contacto@embalajes.cl")
                .telefono("+56911223344")
                .activo(true)
                .build();
    }

    // ── Crear Proveedor ──────────────────────────────────────────────────────

    @Test
    @DisplayName("crear: registra y retorna el proveedor cuando el RUT no existe")
    void crear_rutNuevo_creaExitosamente() {
        when(proveedorRepository.existsByRut("76.123.456-7")).thenReturn(false);
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedorBase);

        ProveedorDTO resultado = proveedorService.crear(proveedorDTOBase);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("76.123.456-7", resultado.getRut());
        assertEquals("Logística & Embalajes S.A.", resultado.getRazonSocial());
        assertTrue(resultado.getActivo());
        verify(proveedorRepository, times(1)).existsByRut("76.123.456-7");
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("crear: lanza excepción cuando el RUT ya se encuentra registrado")
    void crear_rutDuplicado_lanzaExcepcion() {
        when(proveedorRepository.existsByRut("76.123.456-7")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                proveedorService.crear(proveedorDTOBase));

        assertTrue(ex.getMessage().contains("Ya existe un proveedor registrado con el RUT"));
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    // ── Listar Activos ───────────────────────────────────────────────────────

    @Test
    @DisplayName("listarActivos: retorna únicamente los proveedores en estado activo")
    void listarActivos_retornaSoloProveedoresActivos() {
        when(proveedorRepository.findByActivoTrue()).thenReturn(List.of(proveedorBase));

        List<ProveedorDTO> activos = proveedorService.listarActivos();

        assertNotNull(activos);
        assertEquals(1, activos.size());
        assertTrue(activos.get(0).getActivo());
        verify(proveedorRepository, times(1)).findByActivoTrue();
    }

    // ── Borrado Lógico (Desactivar) ──────────────────────────────────────────

    @Test
    @DisplayName("desactivar: cambia el estado activo a false en un proveedor existente")
    void desactivar_proveedorExistente_cambiaEstadoActivoAFalse() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorBase));
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProveedorDTO resultado = proveedorService.desactivar(1L);

        assertNotNull(resultado);
        assertFalse(resultado.getActivo());
        verify(proveedorRepository, times(1)).findById(1L);
        verify(proveedorRepository, times(1)).save(argThat(p -> Boolean.FALSE.equals(p.getActivo())));
    }

    @Test
    @DisplayName("desactivar: lanza excepción si el ID no existe")
    void desactivar_proveedorInexistente_lanzaExcepcion() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                proveedorService.desactivar(99L));

        assertTrue(ex.getMessage().contains("Proveedor no encontrado con ID"));
        verify(proveedorRepository, never()).save(any());
    }
}
