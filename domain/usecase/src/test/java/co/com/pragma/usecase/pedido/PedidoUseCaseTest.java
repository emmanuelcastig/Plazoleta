package co.com.pragma.usecase.pedido;

import co.com.pragma.model.enums.Estado;
import co.com.pragma.model.pedido.Pedido;
import co.com.pragma.model.pedido.gateways.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PedidoUseCaseTest {

    private PedidoRepository pedidoRepository;
    private PedidoUseCase pedidoUseCase;

    @BeforeEach
    void setUp() {
        pedidoRepository = mock(PedidoRepository.class);
        pedidoUseCase = new PedidoUseCase(pedidoRepository);
    }

    @Test
    void crearPedido_exitosoCuandoClienteNoTienePedidoEnProceso() {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(1L);

        when(pedidoRepository.existsByIdClienteAndEstadoIn(eq(1L), anyList())).thenReturn(false);

        pedidoUseCase.crearPedido(pedido);

        // Se debe establecer el estado en PENDIENTE
        assert pedido.getEstado() == Estado.PENDIENTE;

        // Se debe invocar la creación en el repositorio
        verify(pedidoRepository).crearPedido(pedido);
    }

    @Test
    void crearPedido_fallaCuandoClienteYaTienePedidoEnProceso() {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(1L);

        when(pedidoRepository.existsByIdClienteAndEstadoIn(eq(1L), anyList())).thenReturn(true);

        assertThatThrownBy(() -> pedidoUseCase.crearPedido(pedido))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El cliente ya tiene un pedido en proceso");

        // Nunca debe invocar crearPedido en el repositorio
        verify(pedidoRepository, never()).crearPedido(any());
    }
}
