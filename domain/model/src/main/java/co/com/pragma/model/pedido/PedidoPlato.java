package co.com.pragma.model.pedido;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PedidoPlato {
    private Long idPlato;
    private Integer cantidad;
}