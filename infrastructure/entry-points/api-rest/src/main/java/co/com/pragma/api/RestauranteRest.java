package co.com.pragma.api;

import co.com.pragma.api.dto.RestauranteRequest;
import co.com.pragma.api.dto.RestauranteResponse;
import co.com.pragma.api.mapper.RestauranteMapper;
import co.com.pragma.model.restaurante.Restaurante;
import co.com.pragma.usecase.restaurante.RestauranteUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/plazoleta", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class RestauranteRest {

    private final RestauranteUseCase restauranteUseCase;
    private final RestauranteMapper restauranteMapper;

    @PostMapping(path = "/crear/restaurante")
    public ResponseEntity<Void> crearRestaurante(@RequestBody @Valid RestauranteRequest restauranteRequest,
                                                 @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        log.info("Iniciando creacion de restaurante: {}", restauranteRequest.getNombre());
        restauranteUseCase.crearRestaurante(restauranteMapper.toDomain(restauranteRequest), token);
        log.info("Restaurante creado con exito: {}", restauranteRequest.getNombre());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/restaurantes")
    public ResponseEntity<List<RestauranteResponse>> obtenerRestaurantes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        List<Restaurante> restaurantes = restauranteUseCase.obtenerRestaurantes(page, size);

        var response = restaurantes.stream()
                .map(restauranteMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}
