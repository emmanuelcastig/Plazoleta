package co.com.pragma.jpa;

import co.com.pragma.jpa.entity.PedidoEntity;
import co.com.pragma.model.enums.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoJPARepository extends JpaRepository<PedidoEntity, Long> {

    boolean existsByIdClienteAndEstadoIn(Long idCliente, List<Estado> estados);
    Page<PedidoEntity> findByEstadoAndIdRestaurante(Estado estado, Long idRestaurante, Pageable pageable);
}