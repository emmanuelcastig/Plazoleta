package co.com.pragma.usecase.plato;

import co.com.pragma.model.plato.Plato;
import co.com.pragma.model.plato.gateways.PlatoRepository;
import co.com.pragma.model.restaurante.PageResponse;
import co.com.pragma.model.restaurante.Restaurante;
import co.com.pragma.model.restaurante.gateways.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PlatoUseCaseTest {

    private PlatoRepository platoRepository;
    private RestauranteRepository restauranteRepository;
    private PlatoUseCase platoUseCase;

    @BeforeEach
    void setUp() {
        platoRepository = mock(PlatoRepository.class);
        restauranteRepository = mock(RestauranteRepository.class);
        platoUseCase = new PlatoUseCase(platoRepository, restauranteRepository);
    }

    @Test
    void crearPlato_exitoso() {
        Plato plato = new Plato();
        plato.setIdRestaurante(1L);
        Restaurante restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setIdPropietario(10L);

        when(restauranteRepository.obtenerRestaurantePorId(1L)).thenReturn(Optional.of(restaurante));

        platoUseCase.crearPlato(plato, 10L);

        ArgumentCaptor<Plato> captor = ArgumentCaptor.forClass(Plato.class);
        verify(platoRepository).crearPlato(captor.capture());

        assertThat(captor.getValue().getDisponible()).isTrue();
    }

    @Test
    void crearPlato_fallaCuandoRestauranteNoExiste() {
        Plato plato = new Plato();
        plato.setIdRestaurante(99L);

        when(restauranteRepository.obtenerRestaurantePorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> platoUseCase.crearPlato(plato, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El restaurante no existe");
    }

    @Test
    void actualizarPlato_exitoso() {
        Plato plato = new Plato();
        plato.setId(5L);
        plato.setIdRestaurante(1L);
        plato.setPrecio(BigDecimal.valueOf(1000));
        plato.setDescripcion("Original");

        Restaurante restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setIdPropietario(10L);

        when(platoRepository.buscarPlato(5L)).thenReturn(Optional.of(plato));
        when(restauranteRepository.obtenerRestaurantePorId(1L)).thenReturn(Optional.of(restaurante));

        platoUseCase.actualizarPlato(5L, BigDecimal.valueOf(2000), "Nuevo", 10L);

        assertThat(plato.getPrecio()).isEqualByComparingTo("2000");
        assertThat(plato.getDescripcion()).isEqualTo("Nuevo");
        verify(platoRepository).actualizarPlato(plato);
    }

    @Test
    void actualizarPlato_fallaCuandoPlatoNoExiste() {
        when(platoRepository.buscarPlato(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> platoUseCase.actualizarPlato(123L, BigDecimal.TEN, "desc", 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El plato no existe");
    }

    @Test
    void obtenerPlatos_conCategoria() {
        Plato plato = new Plato();
        plato.setId(1L);
        plato.setDescripcion("Plato con categoría");

        PageResponse<Plato> pageResponse = new PageResponse<>(List.of(plato),0, 1, 0, 5);

        when(platoRepository.findByIdRestauranteAndCategoria(1L, "Entradas", 0, 5))
                .thenReturn(pageResponse);

        var resultado = platoUseCase.obtenerPlatos(1L, "Entradas", 0, 5);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getDescripcion()).isEqualTo("Plato con categoría");

        verify(platoRepository).findByIdRestauranteAndCategoria(1L, "Entradas", 0, 5);
        verify(platoRepository, never()).findByIdRestaurante(anyLong(), anyInt(), anyInt());
    }

    @Test
    void obtenerPlatos_sinCategoria() {
        Plato plato = new Plato();
        plato.setId(2L);
        plato.setDescripcion("Plato sin categoría");

        PageResponse<Plato> pageResponse = new PageResponse<>(List.of(plato),0, 5, 0, 5);

        when(platoRepository.findByIdRestaurante(1L, 0, 5))
                .thenReturn(pageResponse);

        var resultado = platoUseCase.obtenerPlatos(1L, null, 0, 5);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getDescripcion()).isEqualTo("Plato sin categoría");

        verify(platoRepository).findByIdRestaurante(1L, 0, 5);
        verify(platoRepository, never()).findByIdRestauranteAndCategoria(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void obtenerPlatos_categoriaVaciaUsaFindByIdRestaurante() {
        PageResponse<Plato> emptyResponse = new PageResponse<>(List.of(),0, 0, 0, 5);

        when(platoRepository.findByIdRestaurante(1L, 0, 5))
                .thenReturn(emptyResponse);

        var resultado = platoUseCase.obtenerPlatos(1L, "   ", 0, 5);

        assertThat(resultado.getContent()).isEmpty();

        verify(platoRepository).findByIdRestaurante(1L, 0, 5);
        verify(platoRepository, never()).findByIdRestauranteAndCategoria(anyLong(), anyString(), anyInt(), anyInt());
    }
}
