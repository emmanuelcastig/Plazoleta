package co.com.pragma.jpa;

import co.com.pragma.jpa.entity.RestauranteEntity;
import co.com.pragma.jpa.helper.AdapterOperations;
import co.com.pragma.model.restaurante.Restaurante;
import co.com.pragma.model.restaurante.gateways.RestauranteRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JPARepositoryAdapter extends AdapterOperations<Restaurante, RestauranteEntity, Long, JPARepository>
 implements RestauranteRepository
{

    public JPARepositoryAdapter(JPARepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, Restaurante.class));
    }

    @Override
    public void crearRestaurante(Restaurante restaurante) {
        repository.save(toData(restaurante));
    }

    @Override
    public Optional<Restaurante> obtenerRestaurantePorId(Long id) {
        return repository.findById(id).map(this::toEntity);
    }
}
