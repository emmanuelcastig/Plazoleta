package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.RestauranteRequest;
import co.com.pragma.api.dto.RestauranteResponse;
import co.com.pragma.model.restaurante.Restaurante;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestauranteMapper {
    Restaurante toDomain(RestauranteRequest request);
    RestauranteResponse toResponse(Restaurante restaurante);
}
