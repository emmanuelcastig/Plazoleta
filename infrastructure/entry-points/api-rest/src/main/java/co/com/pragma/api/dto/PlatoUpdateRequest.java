package co.com.pragma.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlatoUpdateRequest {
    private BigDecimal precio;
    private String descripcion;
}
