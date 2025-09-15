package co.com.pragma.jpa;

import co.com.pragma.jpa.entity.RestauranteEntity;
import co.com.pragma.jpa.helper.AdapterOperations;
import co.com.pragma.model.restaurante.PageResponse;
import co.com.pragma.model.restaurante.Restaurante;
import co.com.pragma.model.restaurante.gateways.RestauranteRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
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

    @Override
    public PageResponse<Restaurante> findAllByOrderByNombreAsc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RestauranteEntity> result = repository.findAllByOrderByNombreAsc(pageable);

        List<Restaurante> content = result.getContent()
                .stream()
                .map(this::toEntity)
                .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
