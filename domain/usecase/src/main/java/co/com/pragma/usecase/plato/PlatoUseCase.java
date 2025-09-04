package co.com.pragma.usecase.plato;

import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.plato.gateways.PlatoRepository;
import co.com.pragma.model.restaurante.Restaurante;
import co.com.pragma.model.restaurante.gateways.RestauranteRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class PlatoUseCase {

    private final PlatoRepository platoRepository;
    private final RestauranteRepository restauranteRepository;

    public void crearPlato(Plato plato) {
        validarRestaurante(plato.getIdRestaurante());
        plato.setDisponible(true);
        platoRepository.crearPlato(plato);
    }

    public void actualizarPlato(Long idPlato, BigDecimal nuevoPrecio, String nuevaDescripcion) {
        Plato plato = platoRepository.buscarPlato(idPlato)
                .orElseThrow(() -> new IllegalArgumentException("El plato no existe"));
        validarRestaurante(plato.getIdRestaurante());
        if (nuevoPrecio != null) {
            plato.setPrecio(nuevoPrecio);
        }
        if (nuevaDescripcion != null && !nuevaDescripcion.isBlank()) {
            plato.setDescripcion(nuevaDescripcion);
        }
        platoRepository.actualizarPlato(plato);
    }

    private boolean validarRestaurante(Long idRestaurante) {
        if (restauranteRepository.obtenerRestaurantePorId(idRestaurante)!= null) {
            return true;
        } else {
            throw new IllegalArgumentException("El restaurante no existe");
        }

    }
}
