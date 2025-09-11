package co.com.pragma.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoPlatoRequest {
    private Long idPlato;
    private Integer cantidad;
}