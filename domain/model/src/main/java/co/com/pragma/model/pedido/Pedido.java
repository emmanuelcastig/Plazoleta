package co.com.pragma.model.pedido;

import co.com.pragma.model.enums.Estado;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Pedido {
    private Long id;
    private Long idCliente;
    private Long idRestaurante;
    private List<PedidoPlato> platos;
    private Estado estado;
}
