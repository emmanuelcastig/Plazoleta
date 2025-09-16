package co.com.pragma.usecase.pedido;

import co.com.pragma.model.consumer.PedidoConsumerGateway;
import co.com.pragma.model.enums.Estado;
import co.com.pragma.model.pedido.Pedido;
import co.com.pragma.model.pedido.PedidoPlato;
import co.com.pragma.model.pedido.gateways.PedidoRepository;
import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.plato.gateways.PlatoRepository;
import co.com.pragma.model.consumer.EmpleadoConsumerGateway;
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
    private PedidoConsumerGateway pedidoConsumerGateway;
    private PedidoUseCase pedidoUseCase;

    @BeforeEach
    void setUp() {
        pedidoRepository = mock(PedidoRepository.class);
        platoRepository = mock(PlatoRepository.class);
        empleadoConsumerGateway = mock(EmpleadoConsumerGateway.class);
        pedidoConsumerGateway = mock(PedidoConsumerGateway.class);

        pedidoUseCase = new PedidoUseCase(
                pedidoRepository,
                platoRepository,
                empleadoConsumerGateway,
                pedidoConsumerGateway
        );
    }

    @Test
    void cambiarEstadoPedidoListo_exitoso() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setIdRestaurante(10L);
        pedido.setEstado(Estado.EN_PREPARACION);

        when(pedidoRepository.buscarPorIdPedido(1L)).thenReturn(Optional.of(pedido));
        when(empleadoConsumerGateway.obtenerRestauranteEmpleado(100L, "tk"))
                .thenReturn(10L);

        String result = pedidoUseCase.cambiarEstadoPedidoListo(1L, 100L, "3001234567", "tk");

        assertThat(pedido.getEstado()).isEqualTo(Estado.LISTO);
        assertThat(pedido.getPin()).isNotNull();
        assertThat(pedido.getPin()).hasSize(4);

        verify(pedidoRepository).actualizarPedido(pedido);
        verify(pedidoConsumerGateway).enviarMensajeSms(eq("3001234567"),
                contains("Tu pedido # 1 está LISTO. PIN:"), eq("tk"));

        assertThat(result).contains("Pin: ");
    }

    @Test
    void cambiarEstadoPedidoListo_fallaCuandoEmpleadoDeOtroRestaurante() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setIdRestaurante(10L);

        when(pedidoRepository.buscarPorIdPedido(1L)).thenReturn(Optional.of(pedido));
        when(empleadoConsumerGateway.obtenerRestauranteEmpleado(200L, "tk"))
                .thenReturn(20L);

        assertThatThrownBy(() -> pedidoUseCase.cambiarEstadoPedidoListo(1L, 200L, "3001234567", "tk"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El empleado no pertenece al restaurante del pedido");

        verify(pedidoRepository, never()).actualizarPedido(any());
        verify(pedidoConsumerGateway, never()).enviarMensajeSms(any(), any(), any());
    }

    @Test
    void crearPedido_exitosoCuandoClienteNoTienePedidoEnProceso() {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(1L);
        pedido.setIdRestaurante(10L);
        pedido.setPlatos(List.of());

        when(pedidoRepository.existsByIdClienteAndEstadoIn(eq(1L), anyList())).thenReturn(false);

        pedidoUseCase.crearPedido(pedido);

        assertThat(pedido.getEstado()).isEqualTo(Estado.PENDIENTE);
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

    @Test
    void asignarPedido_exitosoCuandoEmpleadoPerteneceAlMismoRestaurante() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setIdRestaurante(10L);

        when(pedidoRepository.buscarPorIdPedido(1L)).thenReturn(Optional.of(pedido));
        when(empleadoConsumerGateway.obtenerRestauranteEmpleado(100L, "token"))
                .thenReturn(10L);

        pedidoUseCase.asignarPedido(100L, 1L, "token");

        assertThat(pedido.getEstado()).isEqualTo(Estado.EN_PREPARACION);
        assertThat(pedido.getIdEmpleadoAsignado()).isEqualTo(100L);
        verify(pedidoRepository).actualizarPedido(pedido);
    }

    @Test
    void asignarPedido_fallaCuandoPedidoNoExiste() {
        when(pedidoRepository.buscarPorIdPedido(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoUseCase.asignarPedido(100L, 1L, "tk"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El pedido con id 1 no existe");

        verify(pedidoRepository, never()).actualizarPedido(any());
    }

    @Test
    void asignarPedido_fallaCuandoEmpleadoDeOtroRestaurante() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setIdRestaurante(10L);

        when(pedidoRepository.buscarPorIdPedido(1L)).thenReturn(Optional.of(pedido));
        when(empleadoConsumerGateway.obtenerRestauranteEmpleado(200L, "tk"))
                .thenReturn(20L); // distinto restaurante

        assertThatThrownBy(() -> pedidoUseCase.asignarPedido(200L, 1L, "tk"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El empleado no pertenece al restaurante del pedido");

        verify(pedidoRepository, never()).actualizarPedido(any());
    }

    @Test
    void cambiarEstadoEntregado_exitoso() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setIdRestaurante(10L);
        pedido.setEstado(Estado.LISTO);
        pedido.setPin("1234");

        when(pedidoRepository.buscarPorIdPedido(1L)).thenReturn(Optional.of(pedido));
        when(empleadoConsumerGateway.obtenerRestauranteEmpleado(100L, "tk")).thenReturn(10L);

        pedidoUseCase.cambiarEstadoEntregado(1L, 100L, "1234", "tk");

        assertThat(pedido.getEstado()).isEqualTo(Estado.ENTREGADO);
        verify(pedidoRepository).actualizarPedido(pedido);
    }

    @Test
    void cambiarEstadoEntregado_fallaCuandoPedidoNoExiste() {
        when(pedidoRepository.buscarPorIdPedido(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoUseCase.cambiarEstadoEntregado(1L, 100L, "1234", "tk"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El pedido con id 1 no existe");

        verify(pedidoRepository, never()).actualizarPedido(any());
    }

    @Test
    void cambiarEstadoEntregado_fallaCuandoPedidoYaEntregado() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(Estado.ENTREGADO);

        when(pedidoRepository.buscarPorIdPedido(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoUseCase.cambiarEstadoEntregado(1L, 100L, "1234", "tk"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El pedido ya fue ENTREGADO y no puede modificarse");

        verify(pedidoRepository, never()).actualizarPedido(any());
    }

    @Test
    void cambiarEstadoEntregado_fallaCuandoPedidoNoEstaListo() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(Estado.EN_PREPARACION);

        when(pedidoRepository.buscarPorIdPedido(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoUseCase.cambiarEstadoEntregado(1L, 100L, "1234", "tk"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Solo los pedidos en estado LISTO pueden ser ENTREGADOS");

        verify(pedidoRepository, never()).actualizarPedido(any());
    }

    @Test
    void cambiarEstadoEntregado_fallaCuandoEmpleadoDeOtroRestaurante() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(Estado.LISTO);
        pedido.setIdRestaurante(10L);
        pedido.setPin("1234");

        when(pedidoRepository.buscarPorIdPedido(1L)).thenReturn(Optional.of(pedido));
        when(empleadoConsumerGateway.obtenerRestauranteEmpleado(200L, "tk")).thenReturn(20L);

        assertThatThrownBy(() -> pedidoUseCase.cambiarEstadoEntregado(1L, 200L, "1234", "tk"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El empleado no pertenece al restaurante del pedido");

        verify(pedidoRepository, never()).actualizarPedido(any());
    }

    @Test
    void cambiarEstadoEntregado_fallaCuandoPinIncorrecto() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(Estado.LISTO);
        pedido.setIdRestaurante(10L);
        pedido.setPin("1234");

        when(pedidoRepository.buscarPorIdPedido(1L)).thenReturn(Optional.of(pedido));
        when(empleadoConsumerGateway.obtenerRestauranteEmpleado(100L, "tk")).thenReturn(10L);

        assertThatThrownBy(() -> pedidoUseCase.cambiarEstadoEntregado(1L, 100L, "9999", "tk"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El PIN ingresado es incorrecto");

        verify(pedidoRepository, never()).actualizarPedido(any());
    }
}
