package co.com.pragma.usecase.pedido;

import co.com.pragma.model.enums.Estado;
import co.com.pragma.model.pedido.Pedido;
import co.com.pragma.model.pedido.gateways.PedidoRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public void crearPedido(Pedido pedido) {

        boolean tieneEnProceso = pedidoRepository.existsByIdClienteAndEstadoIn(pedido.getIdCliente(),
                List.of(Estado.PENDIENTE, Estado.EN_PROCESO, Estado.LISTO)
        );

        if (tieneEnProceso) {
            throw new IllegalStateException("El cliente ya tiene un pedido en proceso");
        }

        pedido.setEstado(Estado.PENDIENTE);

        pedidoRepository.crearPedido(pedido);
    }
}