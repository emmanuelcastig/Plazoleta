package co.com.pragma.model.restaurante.gateways;

import co.com.pragma.model.restaurante.Restaurante;

import java.util.List;
import java.util.Optional;

public interface RestauranteRepository {
    void crearRestaurante(Restaurante restaurante);
    Optional<Restaurante> obtenerRestaurantePorId(Long id);
    List<Restaurante> findAllByOrderByNombreAsc(int page, int size);

}
