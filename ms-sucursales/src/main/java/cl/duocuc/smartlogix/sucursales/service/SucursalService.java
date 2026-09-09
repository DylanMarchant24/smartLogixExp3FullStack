package cl.duocuc.smartlogix.sucursales.service;
import cl.duocuc.smartlogix.sucursales.dto.SucursalDTO;
import cl.duocuc.smartlogix.sucursales.model.Sucursal;
import cl.duocuc.smartlogix.sucursales.model.TipoSucursal;
import cl.duocuc.smartlogix.sucursales.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class SucursalService {
    private final SucursalRepository repository;
    public SucursalService(SucursalRepository repository) { this.repository = repository; }
    public List<SucursalDTO> obtenerTodas() { return repository.findAll().stream().map(this::toDTO).toList(); }
    public List<SucursalDTO> obtenerActivas() { return repository.findByActivoTrue().stream().map(this::toDTO).toList(); }
    public List<SucursalDTO> obtenerActivasPorComuna(String comuna) { return repository.findByComunaIgnoreCaseAndActivoTrue(comuna).stream().map(this::toDTO).toList(); }
    public List<SucursalDTO> obtenerActivasPorTipo(TipoSucursal tipo) { return repository.findByTipoAndActivoTrue(tipo).stream().map(this::toDTO).toList(); }
    public SucursalDTO obtenerPorId(Long id) { return toDTO(repository.findById(id).orElseThrow(() -> new RuntimeException("Sucursal no encontrada con id: " + id))); }
    @Transactional public SucursalDTO crear(SucursalDTO dto) {
        if (repository.findByCodigoIgnoreCase(dto.getCodigo()).isPresent()) throw new IllegalArgumentException("Ya existe una sucursal con el código: " + dto.getCodigo());
        Sucursal s = toEntity(dto); s.setId(null); s.setActivo(true); return toDTO(repository.save(s));
    }
    @Transactional public SucursalDTO actualizar(Long id, SucursalDTO dto) {
        Sucursal s = repository.findById(id).orElseThrow(() -> new RuntimeException("Sucursal no encontrada con id: " + id));
        if (!s.getCodigo().equalsIgnoreCase(dto.getCodigo()) && repository.findByCodigoIgnoreCase(dto.getCodigo()).isPresent()) throw new IllegalArgumentException("Ya existe una sucursal con el código: " + dto.getCodigo());
        s.setCodigo(dto.getCodigo()); s.setNombre(dto.getNombre()); s.setDireccion(dto.getDireccion()); s.setComuna(dto.getComuna()); s.setCiudad(dto.getCiudad());
        s.setTelefono(dto.getTelefono()); s.setHorarioAtencion(dto.getHorarioAtencion()); s.setTipo(dto.getTipo());
        return toDTO(repository.save(s));
    }
    @Transactional public SucursalDTO cambiarEstado(Long id, boolean activo) {
        Sucursal s = repository.findById(id).orElseThrow(() -> new RuntimeException("Sucursal no encontrada con id: " + id));
        s.setActivo(activo); return toDTO(repository.save(s));
    }
    private SucursalDTO toDTO(Sucursal s) { return SucursalDTO.builder().id(s.getId()).codigo(s.getCodigo()).nombre(s.getNombre()).direccion(s.getDireccion()).comuna(s.getComuna()).ciudad(s.getCiudad()).telefono(s.getTelefono()).horarioAtencion(s.getHorarioAtencion()).tipo(s.getTipo()).activo(s.getActivo()).build(); }
    private Sucursal toEntity(SucursalDTO d) { return Sucursal.builder().id(d.getId()).codigo(d.getCodigo()).nombre(d.getNombre()).direccion(d.getDireccion()).comuna(d.getComuna()).ciudad(d.getCiudad()).telefono(d.getTelefono()).horarioAtencion(d.getHorarioAtencion()).tipo(d.getTipo()).activo(d.getActivo()!=null?d.getActivo():true).build(); }
}
