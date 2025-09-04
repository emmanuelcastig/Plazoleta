package co.com.pragma.jpa;

import co.com.pragma.jpa.entity.RestauranteEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface JPARepository extends CrudRepository<RestauranteEntity, Long>
        , QueryByExampleExecutor<RestauranteEntity> {
}
