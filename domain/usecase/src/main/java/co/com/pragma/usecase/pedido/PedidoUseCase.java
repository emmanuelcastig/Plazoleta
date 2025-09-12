package co.com.pragma.usecase.pedido;

import co.com.pragma.model.enums.Estado;
import co.com.pragma.model.pedido.Pedido;
import co.com.pragma.model.pedido.PedidoPlato;
import co.com.pragma.model.pedido.gateways.PedidoRepository;
import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.plato.gateways.PlatoRepository;
import co.com.pragma.model.restaurante.consumer.EmpleadoConsumerGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PlatoRepository platoRepository;
    private final EmpleadoConsumerGateway empleadoConsumerGateway;

    public void crearPedido(Pedido pedido) {

        boolean tieneEnProceso = pedidoRepository.existsByIdClienteAndEstadoIn(pedido.getIdCliente(),
                List.of(Estado.PENDIENTE, Estado.EN_PROCESO, Estado.LISTO)
        );

        if (tieneEnProceso) {
            throw new IllegalStateException("El cliente ya tiene un pedido en proceso");
        }

        for (PedidoPlato pedidoPlato : pedido.getPlatos()) {
            Plato plato = platoRepository.buscarPlato(pedidoPlato.getIdPlato())
                    .orElseThrow(() -> new IllegalArgumentException("El plato con id " + pedidoPlato.getIdPlato()
                            + " no existe"));

            if (!plato.getIdRestaurante().equals(pedido.getIdRestaurante())) {
                throw new IllegalArgumentException("El plato con id " + pedidoPlato.getIdPlato() +
                        " no pertenece al restaurante " + pedido.getIdRestaurante());
            }
        }

        pedido.setEstado(Estado.PENDIENTE);

        pedidoRepository.crearPedido(pedido);
    }

    public List<Pedido> listarPedidosPorEstadoYRestaurante(Long idEmpleado, String token, Estado estado,int page, int size) {
        Long idRestaurante = obtenerIdRestaurante(idEmpleado, token);
        return pedidoRepository.findByEstadoAndIdRestaurante(estado, idRestaurante, page, size);
    }

    public Long obtenerIdRestaurante(Long idEmpleado, String token){
        return empleadoConsumerGateway.obtenerRestauranteEmpleado(idEmpleado, token);
    }
}