package co.com.pragma.jpa;

import co.com.pragma.jpa.entity.PedidoEntity;
import co.com.pragma.jpa.helper.AdapterOperations;
import co.com.pragma.model.enums.Estado;
import co.com.pragma.model.pedido.Pedido;
import co.com.pragma.model.pedido.gateways.PedidoRepository;
import co.com.pragma.model.restaurante.PageResponse;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PedidoJPARepositoryAdapter extends AdapterOperations<Pedido, PedidoEntity, Long, PedidoJPARepository>
        implements PedidoRepository
{

    public PedidoJPARepositoryAdapter(PedidoJPARepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, Pedido.class));
    }

    @Override
    public Pedido crearPedido(Pedido pedido) {
        PedidoEntity entity = toData(pedido);
        PedidoEntity saved = repository.save(entity);
        return toEntity(saved);
    }

    @Override
    public boolean existsByIdClienteAndEstadoIn(Long idCliente, List<Estado> estados) {
        return repository.existsByIdClienteAndEstadoIn(idCliente, estados);
    }

    @Override
    public PageResponse<Pedido> findByEstadoAndIdRestaurante(Estado estado, Long idRestaurante, int page, int size ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PedidoEntity> result = repository.findByEstadoAndIdRestaurante(estado, idRestaurante, pageable);

        List<Pedido> content = result.getContent()
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
    public void actualizarPedido(Pedido pedido) {
        this.crearPedido(pedido);
    }

    @Override
    public Optional<Pedido> buscarPorIdPedido(Long id){
        return repository.findById(id).map(this::toEntity);
    }

}