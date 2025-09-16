package co.com.pragma.consumer;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LogPedidoRequest {
    private Long idPedido;
    private Long idCliente;
    private Long idEmpleado;
    private String estadoAnterior;
    private String estadoNuevo;
}
