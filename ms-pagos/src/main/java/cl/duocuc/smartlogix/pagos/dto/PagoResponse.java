package cl.duocuc.smartlogix.pagos.dto;
import cl.duocuc.smartlogix.pagos.model.EstadoPago;
import java.math.BigDecimal;
public record PagoResponse(Long id, Long pedidoId, BigDecimal monto, EstadoPago estado, String codigoTransaccion, String fecha, String motivo) {}
