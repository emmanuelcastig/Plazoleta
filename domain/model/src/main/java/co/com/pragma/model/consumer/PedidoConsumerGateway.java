package co.com.pragma.model.consumer;

public interface PedidoConsumerGateway {
    void enviarMensajeSms(String numeroTelefono,String mensaje, String token);
    void crearLogPedido(Long idPedido, Long idCliente, Long idEmpleado, String estadoAnterior, String estadoNuevo,String token);
}
