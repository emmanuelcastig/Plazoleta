package co.com.pragma.api;

import co.com.pragma.api.dto.PedidoRequest;
import co.com.pragma.model.enums.Estado;
import co.com.pragma.model.pedido.Pedido;
import co.com.pragma.model.pedido.PedidoPlato;
import co.com.pragma.usecase.pedido.PedidoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/plazoleta/pedidos", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Tag(name = "Pedidos", description = "Operaciones para la gestión de pedidos")
public class PedidoRest {

    private final PedidoUseCase pedidoUseCase;

    @Operation(
            summary = "Crear un pedido",
            description = "Permite a un cliente crear un nuevo pedido en un restaurante. " +
                    "Un cliente no puede tener más de un pedido en proceso (pendiente, en proceso o listo).",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content),
                    @ApiResponse(responseCode = "409", description = "El cliente ya tiene un pedido en proceso", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<Void> crearPedido(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Información necesaria para crear un pedido",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PedidoRequest.class))
            )
            @RequestBody PedidoRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        Long idCliente = Long.parseLong(authentication.getName());
        Pedido pedido = Pedido.builder()
                .idCliente(idCliente)
                .idRestaurante(request.getIdRestaurante())
                .platos(
                        request.getPlatos().stream()
                                .map(p -> new PedidoPlato(p.getIdPlato(), p.getCantidad()))
                                .toList()
                )
                .build();

        pedidoUseCase.crearPedido(pedido);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/listar")
    @Operation(
            summary = "Listar pedidos por estado",
            description = "Devuelve la lista de pedidos filtrados por estado, pertenecientes al restaurante del empleado autenticado. La consulta es paginada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pedidos listados exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No se encontraron pedidos", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
            }
    )
    public ResponseEntity<List<Pedido>> listarPedidos(
            @RequestParam(name = "estado") Estado estado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size" ,defaultValue = "10") int size,
            @Parameter(hidden = true) Authentication authentication,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "");
        Long idEmpleado = Long.parseLong(authentication.getName());

        List<Pedido> pedidos = pedidoUseCase.listarPedidosPorEstadoYRestaurante(
                idEmpleado, jwt, estado, page, size
        );

        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(pedidos);
    }

}
