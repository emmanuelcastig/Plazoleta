package co.com.pragma.usecase.pedido;

import co.com.pragma.model.consumer.PedidoConsumerGateway;
import co.com.pragma.model.enums.Estado;
import co.com.pragma.model.pedido.Pedido;
import co.com.pragma.model.pedido.PedidoPlato;
import co.com.pragma.model.pedido.gateways.PedidoRepository;
import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.plato.gateways.PlatoRepository;
import co.com.pragma.model.restaurante.PageResponse;
import co.com.pragma.model.consumer.EmpleadoConsumerGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PlatoRepository platoRepository;
    private final EmpleadoConsumerGateway empleadoConsumerGateway;
    private final PedidoConsumerGateway pedidoConsumerGateway;

    public void crearPedido(Pedido pedido) {

        boolean tieneEnProceso = pedidoRepository.existsByIdClienteAndEstadoIn(pedido.getIdCliente(),
                List.of(Estado.PENDIENTE, Estado.EN_PREPARACION, Estado.LISTO)
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

    public PageResponse<Pedido> listarPedidosPorEstadoYRestaurante(Long idEmpleado, String token, Estado estado, int page, int size) {
        Long idRestaurante = obtenerIdRestaurante(idEmpleado, token);
        return pedidoRepository.findByEstadoAndIdRestaurante(estado, idRestaurante, page, size);
    }

    public Long obtenerIdRestaurante(Long idEmpleado, String token){
        return empleadoConsumerGateway.obtenerRestauranteEmpleado(idEmpleado, token);
    }

    public void asignarPedido(Long idEmpleado, Long idPedido, String token){
        Pedido pedido = pedidoRepository.buscarPorIdPedido(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("El pedido con id " + idPedido + " no existe"));

        Long idRestauranteEmpleado = obtenerIdRestaurante(idEmpleado, token);
        if (!pedido.getIdRestaurante().equals(idRestauranteEmpleado)) {
            throw new IllegalStateException("El empleado no pertenece al restaurante del pedido");
        }

        pedido.setEstado(Estado.EN_PREPARACION);
        pedido.setIdEmpleadoAsignado(idEmpleado);

        pedidoRepository.actualizarPedido(pedido);
    }

    public String cambiarEstadoPedidoListo(Long idPedido,Long idEmpleado,String numeroTelefono,String token){
        Pedido pedido = pedidoRepository.buscarPorIdPedido(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("El pedido con id " + idPedido + " no existe"));

        Long idRestauranteEmpleado = obtenerIdRestaurante(idEmpleado, token);
        if (!pedido.getIdRestaurante().equals(idRestauranteEmpleado)) {
            throw new IllegalStateException("El empleado no pertenece al restaurante del pedido");
        }

        String pin = String.valueOf((int)(Math.random() * 9000) + 1000);
        pedido.setEstado(Estado.LISTO);
        pedido.setPin(pin);
        pedidoRepository.actualizarPedido(pedido);

        String mensaje = "Tu pedido # " + pedido.getId() + " está LISTO. PIN: " + pin;
        pedidoConsumerGateway.enviarMensajeSms(numeroTelefono,mensaje,token);

        return "Pin: " + pin;
    }

    public void cambiarEstadoEntregado(Long idPedido, Long idEmpleado, String pin, String token) {
        Pedido pedido = pedidoRepository.buscarPorIdPedido(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("El pedido con id " + idPedido + " no existe"));

        if (pedido.getEstado() == Estado.ENTREGADO) {
            throw new IllegalStateException("El pedido ya fue ENTREGADO y no puede modificarse");
        }

        if (pedido.getEstado() != Estado.LISTO) {
            throw new IllegalStateException("Solo los pedidos en estado LISTO pueden ser ENTREGADOS");
        }

        Long idRestauranteEmpleado = obtenerIdRestaurante(idEmpleado, token);
        if (!pedido.getIdRestaurante().equals(idRestauranteEmpleado)) {
            throw new IllegalStateException("El empleado no pertenece al restaurante del pedido");
        }

        if (!pedido.getPin().equals(pin)) {
            throw new IllegalArgumentException("El PIN ingresado es incorrecto");
        }

        pedido.setEstado(Estado.ENTREGADO);
        pedidoRepository.actualizarPedido(pedido);
    }

}