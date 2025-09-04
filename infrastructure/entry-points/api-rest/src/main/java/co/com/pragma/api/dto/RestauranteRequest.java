package co.com.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RestauranteRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Pattern(
            regexp = "^(?!\\d+$)[A-Za-z0-9 ]+$",
            message = "El nombre debe contener letras y no puede ser solo números"
    )
    private String nombre;

    @Pattern(
            regexp = "^[0-9]+$",
            message = "El NIT debe contener únicamente números"
    )
    private String nit;

    @NotBlank(message = "La direccion no puede estar vacía")
    private String direccion;

    @Pattern(
            regexp = "^\\+?[0-9]{1,13}$",
            message = "El teléfono debe ser numérico, máximo 13 caracteres y puede comenzar con +"
    )
    private String telefono;

    @NotBlank(message = "La url del logo no puede estar vacía")
    private String urlLogo;

    @NotNull(message = "El id del propietario no puede ser nulo")
    private Long idPropietario;
}
