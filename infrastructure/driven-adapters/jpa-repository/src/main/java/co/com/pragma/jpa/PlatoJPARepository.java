package co.com.pragma.jpa;

import co.com.pragma.jpa.entity.PlatoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatoJPARepository extends JpaRepository<PlatoEntity, Long> {

    Page<PlatoEntity> findByIdRestaurante(Long idRestaurante, Pageable pageable);

    Page<PlatoEntity> findByIdRestauranteAndCategoria(Long idRestaurante, String categoria, Pageable pageable);
}