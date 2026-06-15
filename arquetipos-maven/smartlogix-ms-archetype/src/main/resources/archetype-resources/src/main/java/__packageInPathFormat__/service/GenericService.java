package ${package}.service;

import ${package}.dto.GenericDTO;
import ${package}.model.GenericEntity;
import ${package}.repository.GenericRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenericService {
    private final GenericRepository repository;

    public GenericService(GenericRepository repository) {
        this.repository = repository;
    }

    public List<GenericDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public GenericDTO crear(GenericDTO dto) {
        GenericEntity entity = new GenericEntity();
        entity.setNombre(dto.getNombre());
        return toDTO(repository.save(entity));
    }

    private GenericDTO toDTO(GenericEntity entity) {
        GenericDTO dto = new GenericDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        return dto;
    }
}
