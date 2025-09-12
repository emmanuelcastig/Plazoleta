package co.com.pragma.jpa;

import co.com.pragma.jpa.entity.PedidoEntity;
import co.com.pragma.jpa.helper.AdapterOperations;
import co.com.pragma.model.enums.Estado;
import co.com.pragma.model.pedido.Pedido;
import co.com.pragma.model.pedido.gateways.PedidoRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PedidoJPARepositoryAdapter extends AdapterOperations<Pedido, PedidoEntity, Long, PedidoJPARepository>
        implements PedidoRepository
{

    public PedidoJPARepositoryAdapter(PedidoJPARepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, Pedido.class));
    }

    @Override
    public void crearPedido(Pedido pedido) {
        repository.save(toData(pedido));
    }

    @Override
    public boolean existsByIdClienteAndEstadoIn(Long idCliente, List<Estado> estados) {
        return repository.existsByIdClienteAndEstadoIn(idCliente, estados);
    }

    @Override
    public List<Pedido> findByEstadoAndIdRestaurante(Estado estado, Long idRestaurante, int page, int size ) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByEstadoAndIdRestaurante(estado,idRestaurante,pageable).map(this::toEntity).toList();
    }
}