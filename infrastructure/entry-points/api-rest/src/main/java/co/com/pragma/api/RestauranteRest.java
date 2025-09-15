package co.com.pragma.api;

import co.com.pragma.api.dto.RestauranteRequest;
import co.com.pragma.api.dto.RestauranteResponse;
import co.com.pragma.api.mapper.RestauranteMapper;
import co.com.pragma.model.restaurante.PageResponse;
import co.com.pragma.model.restaurante.Restaurante;
import co.com.pragma.usecase.restaurante.RestauranteUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/plazoleta", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Tag(name = "Restaurantes", description = "Operaciones para la gestión de restaurantes")
public class RestauranteRest {

    private final RestauranteUseCase restauranteUseCase;
    private final RestauranteMapper restauranteMapper;

    @Operation(
            summary = "Crear un restaurante",
            description = "Permite registrar un nuevo restaurante en el sistema. Se requiere un token JWT válido en la cabecera de autorización.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Restaurante creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autorizado o token inválido", content = @Content)
            }
    )
    @PostMapping(path = "/crear/restaurante")
    public ResponseEntity<Void> crearRestaurante(
            @RequestBody(
                    description = "Datos necesarios para crear un restaurante",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RestauranteRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody @Valid RestauranteRequest restauranteRequest,
            @Parameter(description = "Token JWT en formato 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR...")
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        log.info("Iniciando creacion de restaurante: {}", restauranteRequest.getNombre());
        restauranteUseCase.crearRestaurante(restauranteMapper.toDomain(restauranteRequest), token);
        log.info("Restaurante creado con exito: {}", restauranteRequest.getNombre());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
            summary = "Obtener lista de restaurantes",
            description = "Devuelve una lista paginada de restaurantes registrados en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de restaurantes obtenida exitosamente",
                            content = @Content(schema = @Schema(implementation = RestauranteResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos", content = @Content)
            }
    )
    @GetMapping("/restaurantes")
    public ResponseEntity<PageResponse<RestauranteResponse>> obtenerRestaurantes(
            @Parameter(description = "Número de página (empezando desde 0)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,

            @Parameter(description = "Cantidad de registros por página", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        PageResponse<Restaurante> restaurantes = restauranteUseCase.obtenerRestaurantes(page, size);

        var content = restaurantes.getContent()
                .stream()
                .map(restauranteMapper::toResponse)
                .toList();

        PageResponse<RestauranteResponse> response = new PageResponse<>(
                content,
                restaurantes.getPage(),
                restaurantes.getSize(),
                restaurantes.getTotalElements(),
                restaurantes.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }
}
