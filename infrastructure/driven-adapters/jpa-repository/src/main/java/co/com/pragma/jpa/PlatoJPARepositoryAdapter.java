package co.com.pragma.jpa;

import co.com.pragma.jpa.entity.PlatoEntity;
import co.com.pragma.jpa.helper.AdapterOperations;
import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.plato.gateways.PlatoRepository;
import co.com.pragma.model.restaurante.PageResponse;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlatoJPARepositoryAdapter extends AdapterOperations<Plato, PlatoEntity, Long, PlatoJPARepository>
        implements PlatoRepository
{

    public PlatoJPARepositoryAdapter(PlatoJPARepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, Plato.class));
    }

    @Override
    public void crearPlato(Plato plato) {
        repository.save(toData(plato));
    }

    @Override
    public Optional<Plato> buscarPlato(Long id) {
        return repository.findById(id).map(this::toEntity);
    }

    @Override
    public void actualizarPlato(Plato plato) {
        this.crearPlato(plato);
    }

    @Override
    public PageResponse<Plato> findByIdRestaurante(Long idRestaurante, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlatoEntity> result = repository.findByIdRestaurante(idRestaurante, pageable);

        List<Plato> content = result.getContent()
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

    @Override
    public PageResponse<Plato> findByIdRestauranteAndCategoria(Long idRestaurante, String categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlatoEntity> result = repository.findByIdRestauranteAndCategoria(idRestaurante, categoria,pageable);

        List<Plato> content = result.getContent()
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