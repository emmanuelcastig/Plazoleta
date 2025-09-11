package co.com.pragma.api.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequest {
    private Long idRestaurante;
    private List<PedidoPlatoRequest> platos;
}