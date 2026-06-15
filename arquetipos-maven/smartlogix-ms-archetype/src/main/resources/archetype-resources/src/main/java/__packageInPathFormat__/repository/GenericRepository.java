package ${package}.repository;

import ${package}.model.GenericEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericRepository extends JpaRepository<GenericEntity, Long> {
}
