package co.com.pragma.api;
import co.com.pragma.usecase.restaurante.RestauranteUseCase;
import co.com.pragma.api.mapper.RestauranteMapper;
import co.com.pragma.api.dto.RestauranteRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
