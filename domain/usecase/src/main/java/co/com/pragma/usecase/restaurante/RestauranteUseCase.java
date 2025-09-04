package co.com.pragma.usecase.restaurante;

import co.com.pragma.model.restaurante.Restaurante;
import co.com.pragma.model.restaurante.consumer.PropietarioConsumerGateway;
import co.com.pragma.model.restaurante.gateways.RestauranteRepository;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class RestauranteUseCase {

    private final RestauranteRepository restauranteRepository;
    private final PropietarioConsumerGateway propietarioConsumerGateway;

    public void crearRestaurante(Restaurante restaurante) {
        if (validarPropietario(restaurante.getIdPropietario())){
            restauranteRepository.crearRestaurante(restaurante);
        } else {
            throw new IllegalArgumentException("El propietario no existe");
        }
    }

    private boolean validarPropietario(Long idPropietario) {
        return propietarioConsumerGateway.verificarExistenciaPropietario(idPropietario);
    }


}
