package cl.duocuc.smartlogix.pedidos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad Pedido – persistida en db_pedidos.
 * ultimoStockConocido actúa como fallback del Circuit Breaker:
 * si ms-inventario no responde, se almacena el último valor conocido.
 */
@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String skuProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private String clienteEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    /** Último stock conocido – fallback Circuit Breaker */
    private Integer ultimoStockConocido;
}
