package co.com.pragma.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlatoResponse {

    private String nombre;

    private BigDecimal precio;

    private String descripcion;

    private String urlImagen;

    private String categoria;

    private Long idRestaurante;
}
