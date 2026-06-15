package cl.duocuc.smartlogix.inventario;

import cl.duocuc.smartlogix.inventario.dto.ProductoDTO;
import cl.duocuc.smartlogix.inventario.model.Producto;
import cl.duocuc.smartlogix.inventario.repository.ProductoRepository;
import cl.duocuc.smartlogix.inventario.service.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("InventarioService – Pruebas Unitarias")
class InventarioServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Producto productoBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productoBase = new Producto(1L, "Laptop Pro 15", "SKU-001",
                50, new BigDecimal("999990"),
                LocalDateTime.now(), null, "Laptop de alto rendimiento");
    }

    // ── listarTodos ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarTodos: devuelve lista completa de productos")
    void listarTodos_retornaLista() {
        when(productoRepository.findAll()).thenReturn(List.of(productoBase));
        List<ProductoDTO> result = inventarioService.listarTodos();
        assertEquals(1, result.size());
        assertEquals("SKU-001", result.get(0).getSku());
    }

    // ── obtenerPorSku ────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorSku: retorna DTO cuando el SKU existe")
    void obtenerPorSku_existente_retornaDTO() {
        when(productoRepository.findBySku("SKU-001")).thenReturn(Optional.of(productoBase));
        ProductoDTO dto = inventarioService.obtenerPorSku("SKU-001");
        assertNotNull(dto);
        assertEquals("Laptop Pro 15", dto.getNombre());
        assertEquals(50, dto.getStock());
    }

    @Test
    @DisplayName("obtenerPorSku: lanza excepción cuando el SKU no existe")
    void obtenerPorSku_noExistente_lanzaExcepcion() {
        when(productoRepository.findBySku("SKU-999")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventarioService.obtenerPorSku("SKU-999"));
        assertTrue(ex.getMessage().contains("SKU-999"));
    }

    // ── crear ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear: persiste y retorna producto cuando el SKU es nuevo")
    void crear_skuNuevo_persisteProducto() {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Monitor 4K");
        dto.setSku("SKU-002");
        dto.setStock(20);
        dto.setPrecio(new BigDecimal("299990"));

        when(productoRepository.existsBySku("SKU-002")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoBase);

        ProductoDTO result = inventarioService.crear(dto);
        assertNotNull(result);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("crear: lanza excepción cuando el SKU ya existe")
    void crear_skuDuplicado_lanzaExcepcion() {
        ProductoDTO dto = new ProductoDTO();
        dto.setSku("SKU-001");
        when(productoRepository.existsBySku("SKU-001")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> inventarioService.crear(dto));
        verify(productoRepository, never()).save(any());
    }

    // ── reducirStock ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("reducirStock: reduce correctamente cuando hay stock suficiente")
    void reducirStock_stockSuficiente_actualiza() {
        when(productoRepository.reducirStock("SKU-001", 5)).thenReturn(1);
        when(productoRepository.findBySku("SKU-001")).thenReturn(Optional.of(productoBase));

        ProductoDTO result = inventarioService.reducirStock("SKU-001", 5);
        assertNotNull(result);
        verify(productoRepository).reducirStock("SKU-001", 5);
    }

    @Test
    @DisplayName("reducirStock: lanza excepción cuando no hay stock suficiente")
    void reducirStock_stockInsuficiente_lanzaExcepcion() {
        when(productoRepository.reducirStock("SKU-001", 100)).thenReturn(0);
        when(productoRepository.findBySku("SKU-001")).thenReturn(Optional.of(productoBase));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventarioService.reducirStock("SKU-001", 100));
        assertTrue(ex.getMessage().toLowerCase().contains("insuficiente"));
    }

    // ── eliminar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar: invoca delete cuando el producto existe")
    void eliminar_productoExistente_eliminaCorrectamente() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);
        assertDoesNotThrow(() -> inventarioService.eliminar(1L));
        verify(productoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar: lanza excepción cuando el producto no existe")
    void eliminar_productoInexistente_lanzaExcepcion() {
        when(productoRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> inventarioService.eliminar(99L));
    }
}
