package co.com.pragma.usecase.restaurante;

import co.com.pragma.model.restaurante.PageResponse;
import co.com.pragma.model.restaurante.Restaurante;
import co.com.pragma.model.restaurante.consumer.PropietarioConsumerGateway;
import co.com.pragma.model.restaurante.gateways.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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

        PageResponse<Restaurante> pageResponse =
                new PageResponse<>(Arrays.asList(r1, r2),0, 10, 2, 10);

        when(restauranteRepository.findAllByOrderByNombreAsc(0, 10))
                .thenReturn(pageResponse);

        PageResponse<Restaurante> resultado = restauranteUseCase.obtenerRestaurantes(0, 10);

        assertThat(resultado.getContent()).containsExactly(r1, r2);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);

        verify(restauranteRepository, times(1)).findAllByOrderByNombreAsc(0, 10);
    }
}
