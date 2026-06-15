package cl.duocuc.smartlogix.envios.service;

import cl.duocuc.smartlogix.envios.dto.EnvioDTO;
import cl.duocuc.smartlogix.envios.model.Envio;
import cl.duocuc.smartlogix.envios.model.EstadoEnvio;
import cl.duocuc.smartlogix.envios.repository.EnvioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de Envíos.
 * PATRÓN: Repository Pattern – usa EnvioRepository para persistir envíos.
 * Genera código de seguimiento único (UUID) al crear cada envío.
 */
@Service
public class EnvioService {

    private final EnvioRepository envioRepository;

    public EnvioService(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    // ── Consultas ─────────────────────────────────────────────────────────────

    public List<EnvioDTO> listar() {
        return envioRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public EnvioDTO obtenerPorId(Long id) {
        return toDTO(envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado: " + id)));
    }

    public List<EnvioDTO> listarPorPedido(Long pedidoId) {
        return envioRepository.findByPedidoId(pedidoId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public EnvioDTO buscarPorCodigo(String codigo) {
        return toDTO(envioRepository.findByCodigoSeguimiento(codigo)
                .orElseThrow(() -> new RuntimeException("Código de seguimiento no encontrado: " + codigo)));
    }

    // ── Escritura ─────────────────────────────────────────────────────────────

    @Transactional
    public EnvioDTO crearEnvio(EnvioDTO dto) {
        Envio envio = new Envio();
        envio.setPedidoId(dto.getPedidoId());
        envio.setTransportista(dto.getTransportista());
        envio.setDireccionDestino(dto.getDireccionDestino());
        envio.setEstado(EstadoEnvio.PENDIENTE);
        // Genera código de seguimiento único
        envio.setCodigoSeguimiento("SL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return toDTO(envioRepository.save(envio));
    }

    @Transactional
    public EnvioDTO actualizarEstado(Long id, EstadoEnvio nuevoEstado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado: " + id));
        envio.setEstado(nuevoEstado);
        envio.setFechaActualizacion(LocalDateTime.now());
        if (nuevoEstado == EstadoEnvio.ENTREGADO) {
            envio.setFechaEntrega(LocalDateTime.now());
        }
        return toDTO(envioRepository.save(envio));
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private EnvioDTO toDTO(Envio e) {
        EnvioDTO dto = new EnvioDTO();
        dto.setId(e.getId());
        dto.setPedidoId(e.getPedidoId());
        dto.setTransportista(e.getTransportista());
        dto.setDireccionDestino(e.getDireccionDestino());
        dto.setEstado(e.getEstado() != null ? e.getEstado().name() : null);
        dto.setCodigoSeguimiento(e.getCodigoSeguimiento());
        dto.setFechaCreacion(e.getFechaCreacion() != null ? e.getFechaCreacion().toString() : null);
        dto.setFechaEntrega(e.getFechaEntrega() != null ? e.getFechaEntrega().toString() : null);
        return dto;
    }
}
