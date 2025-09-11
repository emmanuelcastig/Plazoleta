package co.com.pragma.model.plato.gateways;

import co.com.pragma.model.plato.Plato;

import java.util.List;
import java.util.Optional;

public interface PlatoRepository {
    void crearPlato(Plato plato);

    Optional<Plato> buscarPlato(Long id);

    void actualizarPlato(Plato plato);

    List<Plato> findByIdRestaurante(Long idRestaurante, int page, int size);

    List<Plato> findByIdRestauranteAndCategoria(Long idRestaurante, String categoria, int page, int size);
}
