package co.com.pragma.jpa.entity;

import co.com.pragma.model.enums.Estado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "pedidos")
public class PedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idCliente;

    private Long idRestaurante;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pedido_id")
    private List<PedidoPlatoEntity> platos;

    @Enumerated(EnumType.STRING)
    private Estado estado;
}

