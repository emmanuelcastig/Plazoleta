package co.com.pragma.model.pedido.gateways;

import co.com.pragma.model.enums.Estado;
import co.com.pragma.model.pedido.Pedido;
import co.com.pragma.model.restaurante.PageResponse;

import java.util.List;

public interface PedidoRepository {
    void crearPedido(Pedido pedido);
    boolean existsByIdClienteAndEstadoIn(Long idCliente, List<Estado> estados);
    PageResponse<Pedido> findByEstadoAndIdRestaurante(Estado estado, Long idRestaurante, int page, int size);

}
