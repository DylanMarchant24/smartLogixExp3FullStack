package cl.duocuc.smartlogix.cupones.service;

import cl.duocuc.smartlogix.cupones.dto.*;
import cl.duocuc.smartlogix.cupones.model.Cupon;
import cl.duocuc.smartlogix.cupones.model.TipoDescuento;
import cl.duocuc.smartlogix.cupones.repository.CuponRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuponService {

    private final CuponRepository cuponRepository;

    @Transactional
    public CuponResponseDTO crear(CuponRequestDTO request) {
        if (cuponRepository.existsByCodigoIgnoreCase(request.getCodigo())) {
            throw new EntityExistsException("Ya existe un cupon con el codigo " + request.getCodigo());
        }
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        if (request.getTipo() == TipoDescuento.PORCENTAJE
                && request.getValor().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Un descuento porcentual no puede superar 100");
        }

        Cupon cupon = Cupon.builder()
                .codigo(request.getCodigo().toUpperCase())
                .tipo(request.getTipo())
                .valor(request.getValor())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .activo(true)
                .build();

        return CuponResponseDTO.fromEntity(cuponRepository.save(cupon));
    }

    @Transactional(readOnly = true)
    public List<CuponResponseDTO> listar() {
        return cuponRepository.findAll().stream()
                .map(CuponResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public CuponResponseDTO obtenerPorId(Long id) {
        Cupon cupon = cuponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cupon no encontrado con id " + id));
        return CuponResponseDTO.fromEntity(cupon);
    }

    @Transactional
    public CuponResponseDTO desactivar(Long id) {
        Cupon cupon = cuponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cupon no encontrado con id " + id));
        cupon.setActivo(false);
        return CuponResponseDTO.fromEntity(cuponRepository.save(cupon));
    }

    @Transactional(readOnly = true)
    public ValidacionCuponResponseDTO validar(ValidacionCuponRequestDTO request) {
        ValidacionCuponResponseDTO response = new ValidacionCuponResponseDTO();
        response.setCodigo(request.getCodigo());
        response.setMontoCompra(request.getMontoCompra());

        Cupon cupon = cuponRepository.findByCodigoIgnoreCase(request.getCodigo()).orElse(null);

        if (cupon == null) {
            response.setValido(false);
            response.setMotivoRechazo("El codigo de cupon no existe");
            response.setMontoRebaja(BigDecimal.ZERO);
            response.setMontoFinal(request.getMontoCompra());
            return response;
        }

        if (!cupon.estaVigente(LocalDate.now())) {
            response.setValido(false);
            response.setMotivoRechazo("El cupon no esta vigente o esta inactivo");
            response.setTipo(cupon.getTipo());
            response.setMontoRebaja(BigDecimal.ZERO);
            response.setMontoFinal(request.getMontoCompra());
            return response;
        }

        BigDecimal rebaja = calcularRebaja(cupon, request.getMontoCompra());
        BigDecimal montoFinal = request.getMontoCompra().subtract(rebaja).max(BigDecimal.ZERO);

        response.setValido(true);
        response.setTipo(cupon.getTipo());
        response.setMontoRebaja(rebaja.setScale(2, RoundingMode.HALF_UP));
        response.setMontoFinal(montoFinal.setScale(2, RoundingMode.HALF_UP));
        return response;
    }

    private BigDecimal calcularRebaja(Cupon cupon, BigDecimal montoCompra) {
        if (cupon.getTipo() == TipoDescuento.PORCENTAJE) {
            return montoCompra
                    .multiply(cupon.getValor())
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        }
        return cupon.getValor().min(montoCompra);
    }
}
