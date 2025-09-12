package co.com.pragma.usecase.pedido;

import co.com.pragma.model.enums.Estado;
import co.com.pragma.model.pedido.Pedido;
import co.com.pragma.model.pedido.PedidoPlato;
import co.com.pragma.model.pedido.gateways.PedidoRepository;
import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.plato.gateways.PlatoRepository;
import co.com.pragma.model.restaurante.consumer.EmpleadoConsumerGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class PedidoUseCaseTest {

    private PedidoRepository pedidoRepository;
    private PlatoRepository platoRepository;
    private EmpleadoConsumerGateway empleadoConsumerGateway;
    private PedidoUseCase pedidoUseCase;

    @BeforeEach
    void setUp() {
        pedidoRepository = mock(PedidoRepository.class);
        platoRepository = mock(PlatoRepository.class);
        empleadoConsumerGateway = mock(EmpleadoConsumerGateway.class);
        pedidoUseCase = new PedidoUseCase(pedidoRepository, platoRepository, empleadoConsumerGateway);
    }

    @Test
    void crearPedido_exitosoCuandoClienteNoTienePedidoEnProceso() {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(1L);
        pedido.setIdRestaurante(10L);
        pedido.setPlatos(List.of());

        when(pedidoRepository.existsByIdClienteAndEstadoIn(eq(1L), anyList())).thenReturn(false);

        pedidoUseCase.crearPedido(pedido);

        assert pedido.getEstado() == Estado.PENDIENTE;
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

        verify(pedidoRepository, never()).crearPedido(any());
    }

    @Test
    void crearPedido_fallaCuandoPlatoNoExiste() {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(1L);
        pedido.setIdRestaurante(10L);
        pedido.setPlatos(List.of(new PedidoPlato(99L, 2)));

        when(pedidoRepository.existsByIdClienteAndEstadoIn(eq(1L), anyList())).thenReturn(false);
        when(platoRepository.buscarPlato(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoUseCase.crearPedido(pedido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El plato con id 99 no existe");

        verify(pedidoRepository, never()).crearPedido(any());
    }

    @Test
    void crearPedido_fallaCuandoPlatoPerteneceAOtroRestaurante() {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(1L);
        pedido.setIdRestaurante(10L);
        pedido.setPlatos(List.of(new PedidoPlato(5L, 1)));

        Plato plato = new Plato();
        plato.setId(5L);
        plato.setIdRestaurante(20L); // otro restaurante

        when(pedidoRepository.existsByIdClienteAndEstadoIn(eq(1L), anyList())).thenReturn(false);
        when(platoRepository.buscarPlato(5L)).thenReturn(Optional.of(plato));

        assertThatThrownBy(() -> pedidoUseCase.crearPedido(pedido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El plato con id 5 no pertenece al restaurante 10");

        verify(pedidoRepository, never()).crearPedido(any());
    }

    @Test
    void listarPedidosPorEstadoYRestaurante_delegaEnRepositorio() {
        Long idEmpleado = 100L;
        String token = "token123";
        Long idRestaurante = 50L;

        when(empleadoConsumerGateway.obtenerRestauranteEmpleado(idEmpleado, token))
                .thenReturn(idRestaurante);

        pedidoUseCase.listarPedidosPorEstadoYRestaurante(idEmpleado, token, Estado.PENDIENTE, 0, 5);

        verify(pedidoRepository).findByEstadoAndIdRestaurante(Estado.PENDIENTE, idRestaurante, 0, 5);
    }

    @Test
    void obtenerIdRestaurante_retornaValorDelGateway() {
        when(empleadoConsumerGateway.obtenerRestauranteEmpleado(200L, "tk"))
                .thenReturn(30L);

        Long result = pedidoUseCase.obtenerIdRestaurante(200L, "tk");

        assertThat(result).isEqualTo(30L);
        verify(empleadoConsumerGateway).obtenerRestauranteEmpleado(200L, "tk");
    }

}
