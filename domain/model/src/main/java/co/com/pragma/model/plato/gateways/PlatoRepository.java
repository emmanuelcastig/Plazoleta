package co.com.pragma.model.plato.gateways;

import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.restaurante.PageResponse;

import java.util.Optional;

public interface PlatoRepository {
    void crearPlato(Plato plato);

    Optional<Plato> buscarPlato(Long id);

    void actualizarPlato(Plato plato);

    PageResponse<Plato> findByIdRestaurante(Long idRestaurante, int page, int size);

    PageResponse<Plato> findByIdRestauranteAndCategoria(Long idRestaurante, String categoria, int page, int size);
}
