package cl.duocuc.smartlogix.auditoria.service;

import cl.duocuc.smartlogix.auditoria.model.Auditoria;
import cl.duocuc.smartlogix.auditoria.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository repository;

    public AuditoriaService(AuditoriaRepository repository) {
        this.repository = repository;
    }

    public List<Auditoria> listarTodas() {
        return repository.findAll();
    }

    public Auditoria guardarAuditoria(Auditoria auditoria) {
        return repository.save(auditoria);
    }
}