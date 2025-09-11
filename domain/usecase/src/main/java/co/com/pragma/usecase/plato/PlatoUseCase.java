package co.com.pragma.usecase.plato;

import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.plato.gateways.PlatoRepository;
import co.com.pragma.model.restaurante.Restaurante;
import co.com.pragma.model.restaurante.gateways.RestauranteRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
public class PlatoUseCase {

    private final PlatoRepository platoRepository;
    private final RestauranteRepository restauranteRepository;

    public void crearPlato(Plato plato, Long propietarioId) {
        validarRestaurante(plato.getIdRestaurante(), propietarioId);
        plato.setDisponible(true);
        platoRepository.crearPlato(plato);
    }

    public void actualizarPlato(Long idPlato, BigDecimal nuevoPrecio, String nuevaDescripcion, Long propietarioId) {
        Plato plato = platoRepository.buscarPlato(idPlato)
                .orElseThrow(() -> new IllegalArgumentException("El plato no existe"));
        validarRestaurante(plato.getIdRestaurante(), propietarioId);
        if (nuevoPrecio != null) {
            plato.setPrecio(nuevoPrecio);
        }
        if (nuevaDescripcion != null && !nuevaDescripcion.isBlank()) {
            plato.setDescripcion(nuevaDescripcion);
        }
        platoRepository.actualizarPlato(plato);
    }

    private void validarRestaurante(Long idRestaurante, Long idPropietario) {
        Optional<Restaurante> restauranteOpt = restauranteRepository.obtenerRestaurantePorId(idRestaurante);
        Restaurante restaurante = restauranteOpt.orElseThrow(
                () -> new IllegalArgumentException("El restaurante no existe")
        );

        if (!restaurante.getIdPropietario().equals(idPropietario)) {
            throw new IllegalArgumentException("El restaurante no pertenece al propietario autenticado");
        }
    }

}
