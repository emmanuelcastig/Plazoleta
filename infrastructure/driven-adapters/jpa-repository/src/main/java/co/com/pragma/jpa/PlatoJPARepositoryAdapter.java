package co.com.pragma.jpa;

import co.com.pragma.jpa.entity.PlatoEntity;
import co.com.pragma.jpa.helper.AdapterOperations;
import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.plato.gateways.PlatoRepository;
import org.reactivecommons.utils.ObjectMapper;
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
    public List<Plato> findByIdRestaurante(Long idRestaurante, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByIdRestaurante(idRestaurante, pageable).map(this::toEntity).toList();
    }

    @Override
    public List<Plato> findByIdRestauranteAndCategoria(Long idRestaurante, String categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByIdRestauranteAndCategoria(idRestaurante,categoria,pageable).map(this::toEntity).toList();
    }
}