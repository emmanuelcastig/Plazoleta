package co.com.pragma.jpa;

import co.com.pragma.jpa.entity.PlatoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface PlatoJPARepository  extends CrudRepository<PlatoEntity, Long>
        , QueryByExampleExecutor<PlatoEntity> {
}