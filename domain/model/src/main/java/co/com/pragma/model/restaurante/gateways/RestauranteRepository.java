package co.com.pragma.model.restaurante.gateways;

import co.com.pragma.model.restaurante.Restaurante;

import java.util.Optional;

public interface RestauranteRepository {
    void crearRestaurante(Restaurante restaurante);
    Optional<Restaurante> obtenerRestaurantePorId(Long id);

}
