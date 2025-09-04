package co.com.pragma.usecase.plato;

import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.plato.gateways.PlatoRepository;
import co.com.pragma.model.restaurante.Restaurante;
import co.com.pragma.model.restaurante.gateways.RestauranteRepository;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class PlatoUseCase {

    private final PlatoRepository platoRepository;
    private final RestauranteRepository restauranteRepository;

    public void crearPlato(Plato plato) {
        Restaurante restaurante = restauranteRepository.obtenerRestaurantePorId(plato.getIdRestaurante())
                        .orElseThrow(() -> new IllegalArgumentException("El restaurante no existe"));
        plato.setDisponible(true);
        platoRepository.crearPlato(plato);
    }
}
