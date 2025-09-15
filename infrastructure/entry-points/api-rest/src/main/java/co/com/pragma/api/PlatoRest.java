package co.com.pragma.api;

import co.com.pragma.api.dto.PlatoRequest;
import co.com.pragma.api.dto.PlatoResponse;
import co.com.pragma.api.dto.PlatoUpdateRequest;
import co.com.pragma.api.mapper.PlatoMapper;
import co.com.pragma.model.restaurante.PageResponse;
import co.com.pragma.usecase.plato.PlatoUseCase;
import org.springframework.security.core.Authentication;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/plazoleta", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Tag(name = "Platos", description = "Operaciones relacionadas con la gestión de platos en la plazoleta")
public class PlatoRest {

    private final PlatoUseCase platoUseCase;
    private final PlatoMapper platoMapper;

    @Operation(
            summary = "Crear un plato",
            description = "Permite al propietario de un restaurante crear un nuevo plato.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Plato creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
            }
    )
    @PostMapping(path = "/crear/plato")
    public ResponseEntity<Void> crearPlato(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Información del nuevo plato",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlatoRequest.class))
            )
            @RequestBody @Valid PlatoRequest platoRequest,
            @Parameter(hidden = true) Authentication authentication
    ) {
        Long propietarioId = Long.parseLong(authentication.getName());
        log.info("Propietario {} intenta crear el plato {}", propietarioId, platoRequest.getNombre());
        platoUseCase.crearPlato(platoMapper.toDomain(platoRequest), propietarioId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
            summary = "Actualizar un plato",
            description = "Permite al propietario de un restaurante actualizar un plato existente.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Plato actualizado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Plato no encontrado", content = @Content)
            }
    )
    @PutMapping("/actualizar/plato/{idPlato}")
    public ResponseEntity<Void> actualizarPlato(
            @Parameter(description = "ID del plato a actualizar", example = "10")
            @PathVariable("idPlato") Long idPlato,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos a actualizar del plato",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlatoUpdateRequest.class))
            )
            @RequestBody PlatoUpdateRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        Long propietarioId = Long.parseLong(authentication.getName());
        log.info("Propietario {} intenta actualizar el plato {}", propietarioId, idPlato);
        platoUseCase.actualizarPlato(idPlato, request.getPrecio(), request.getDescripcion(), propietarioId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Listar platos de un restaurante",
            description = "Obtiene la lista de platos de un restaurante, con la posibilidad de filtrar por categoría y paginar los resultados.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de platos obtenida exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PlatoResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros inválidos en la solicitud",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autorizado",
                            content = @Content
                    )
            }
    )
    @GetMapping("/platos")
    public ResponseEntity<PageResponse<PlatoResponse>> listarPlatos(
            @Parameter(description = "ID del restaurante", example = "5", required = true)
            @RequestParam(name = "idRestaurante") Long idRestaurante,

            @Parameter(description = "Categoría de los platos a filtrar", example = "Entradas")
            @RequestParam(name = "categoria", required = false) String categoria,

            @Parameter(description = "Número de página (para paginación)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,

            @Parameter(description = "Cantidad de registros por página", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("Consultando platos del restaurante {} con categoria={} page={} size={}",
                idRestaurante, categoria, page, size);

        var platos = platoUseCase.obtenerPlatos(idRestaurante, categoria, page, size);

        var content = platos.getContent()
                .stream()
                .map(platoMapper::toResponse)
                .toList();

        PageResponse<PlatoResponse> response = new PageResponse<>(
                content,
                platos.getPage(),
                platos.getSize(),
                platos.getTotalElements(),
                platos.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
}
