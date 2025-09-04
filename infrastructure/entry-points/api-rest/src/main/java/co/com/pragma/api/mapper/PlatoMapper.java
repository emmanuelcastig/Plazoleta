package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.PlatoRequest;
import co.com.pragma.model.plato.Plato;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatoMapper {
    Plato toDomain(PlatoRequest request);
}
