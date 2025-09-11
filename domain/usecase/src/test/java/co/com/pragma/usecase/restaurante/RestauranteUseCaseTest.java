package co.com.pragma.usecase.restaurante;

import co.com.pragma.model.restaurante.Restaurante;
import co.com.pragma.model.restaurante.consumer.PropietarioConsumerGateway;
import co.com.pragma.model.restaurante.gateways.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestauranteUseCaseTest {

    private RestauranteRepository restauranteRepository;
    private PropietarioConsumerGateway propietarioConsumerGateway;
    private RestauranteUseCase restauranteUseCase;

    @BeforeEach
    void setUp() {
        restauranteRepository = mock(RestauranteRepository.class);
        propietarioConsumerGateway = mock(PropietarioConsumerGateway.class);
        restauranteUseCase = new RestauranteUseCase(restauranteRepository, propietarioConsumerGateway);
    }

    @Test
    void crearRestaurante_exitosoCuandoPropietarioExiste() {
        Restaurante restaurante = new Restaurante();
        restaurante.setIdPropietario(1L);

        when(propietarioConsumerGateway.verificarExistenciaPropietario(1L, "tokenValido"))
                .thenReturn(true);

        restauranteUseCase.crearRestaurante(restaurante, "tokenValido");

        verify(restauranteRepository, times(1)).crearRestaurante(restaurante);
    }

    @Test
    void crearRestaurante_fallaCuandoPropietarioNoExiste() {
        Restaurante restaurante = new Restaurante();
        restaurante.setIdPropietario(1L);

        when(propietarioConsumerGateway.verificarExistenciaPropietario(1L, "tokenInvalido"))
                .thenReturn(false);

        assertThatThrownBy(() -> restauranteUseCase.crearRestaurante(restaurante, "tokenInvalido"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El propietario no existe");

        verify(restauranteRepository, never()).crearRestaurante(any());
    }

    @Test
    void obtenerRestaurantes_devuelveListaOrdenada() {
        Restaurante r1 = new Restaurante();
        r1.setNombre("A");

        Restaurante r2 = new Restaurante();
        r2.setNombre("B");

        when(restauranteRepository.findAllByOrderByNombreAsc(0, 10))
                .thenReturn(Arrays.asList(r1, r2));

        List<Restaurante> resultado = restauranteUseCase.obtenerRestaurantes(0, 10);

        assertThat(resultado).containsExactly(r1, r2);
        verify(restauranteRepository, times(1)).findAllByOrderByNombreAsc(0, 10);
    }
}
