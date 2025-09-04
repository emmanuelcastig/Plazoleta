package co.com.pragma.api;

import co.com.pragma.api.dto.PlatoRequest;
import co.com.pragma.api.dto.PlatoUpdateRequest;
import co.com.pragma.api.dto.RestauranteRequest;
import co.com.pragma.api.mapper.PlatoMapper;
import co.com.pragma.api.mapper.RestauranteMapper;
import co.com.pragma.usecase.plato.PlatoUseCase;
import co.com.pragma.usecase.restaurante.RestauranteUseCase;
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
public class PlatoRest {

    private final PlatoUseCase platoUseCase;
    private final PlatoMapper platoMapper;

    @PostMapping(path = "/crear/plato")
    public ResponseEntity<Void> crearPlato(@RequestBody @Valid PlatoRequest platoRequest) {
        log.info("Iniciando creacion del plato: {}", platoRequest.getNombre());
        platoUseCase.crearPlato(platoMapper.toDomain(platoRequest));
        log.info("Plato creado con exito: {}", platoRequest.getNombre());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/actualizar/plato/{idPlato}")
    public ResponseEntity<Void> actualizarPlato(@PathVariable("idPlato") Long idPlato, @RequestBody PlatoUpdateRequest request) {
        log.info("Iniciando actualizacion del plato con id: {}", idPlato);
        platoUseCase.actualizarPlato(idPlato,request.getPrecio(),request.getDescripcion());
        log.info("Plato actualizado con exito: {}", idPlato);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
