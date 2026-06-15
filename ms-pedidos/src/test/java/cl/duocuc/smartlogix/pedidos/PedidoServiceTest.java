package cl.duocuc.smartlogix.pedidos;

import cl.duocuc.smartlogix.pedidos.dto.PedidoDTO;
import cl.duocuc.smartlogix.pedidos.factory.PedidoFactory;
import cl.duocuc.smartlogix.pedidos.model.EstadoPedido;
import cl.duocuc.smartlogix.pedidos.model.Pedido;
import cl.duocuc.smartlogix.pedidos.repository.PedidoRepository;
import cl.duocuc.smartlogix.pedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PedidoService – Pruebas Unitarias")
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private RestTemplate restTemplate;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedidoBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pedidoBase = new Pedido(1L, "SKU-001", 2, "cliente@test.cl",
                EstadoPedido.APROBADO, LocalDateTime.now(), null, 50);
    }

    // ── obtenerPorId ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId: retorna DTO cuando el pedido existe")
    void obtenerPorId_existente_retornaDTO() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoBase));
        PedidoDTO dto = pedidoService.obtenerPorId(1L);
        assertNotNull(dto);
        assertEquals("SKU-001", dto.getSkuProducto());
        assertEquals("APROBADO", dto.getEstado());
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepción cuando el pedido no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> pedidoService.obtenerPorId(99L));
    }

    // ── listarTodos ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarTodos: devuelve todos los pedidos")
    void listarTodos_retornaListaCompleta() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedidoBase));
        List<PedidoDTO> result = pedidoService.listarTodos();
        assertEquals(1, result.size());
    }

    // ── cambiarEstado ────────────────────────────────────────────────────────

    @Test
    @DisplayName("cambiarEstado: actualiza estado y fecha de modificación")
    void cambiarEstado_actualizaCorrectamente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoBase));
        when(pedidoRepository.save(any())).thenReturn(pedidoBase);
        pedidoService.cambiarEstado(1L, EstadoPedido.DESPACHADO);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    // ── Factory Method ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Factory crearNuevo: estado inicial CREADO")
    void factoryMethod_crearNuevo_estadoCREADO() {
        Pedido p = PedidoFactory.crearNuevo("SKU-001", 3, "test@cl.cl");
        assertEquals(EstadoPedido.CREADO, p.getEstado());
        assertNotNull(p.getFechaCreacion());
        assertNull(p.getUltimoStockConocido());
    }

    @Test
    @DisplayName("Factory crearValidado: estado VALIDADO con stock registrado")
    void factoryMethod_crearValidado_stockRegistrado() {
        Pedido p = PedidoFactory.crearValidado("SKU-001", 3, "test@cl.cl", 50);
        assertEquals(EstadoPedido.VALIDADO, p.getEstado());
        assertEquals(50, p.getUltimoStockConocido());
    }

    @Test
    @DisplayName("Factory crearAprobado: estado APROBADO con stock registrado")
    void factoryMethod_crearAprobado_estadoAPROBADO() {
        Pedido p = PedidoFactory.crearAprobado("SKU-001", 3, "test@cl.cl", 50);
        assertEquals(EstadoPedido.APROBADO, p.getEstado());
        assertEquals(50, p.getUltimoStockConocido());
    }

    // ── listarPorEstado ──────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPorEstado: filtra correctamente por estado")
    void listarPorEstado_retornaFiltrados() {
        when(pedidoRepository.findByEstado(EstadoPedido.APROBADO))
                .thenReturn(List.of(pedidoBase));
        List<PedidoDTO> result = pedidoService.listarPorEstado(EstadoPedido.APROBADO);
        assertEquals(1, result.size());
        assertEquals("APROBADO", result.get(0).getEstado());
    }
}
