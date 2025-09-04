package co.com.pragma.jpa;

import co.com.pragma.jpa.entity.PlatoEntity;
import co.com.pragma.jpa.helper.AdapterOperations;
import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.plato.gateways.PlatoRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

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
}