package co.com.pragma.model.plato.gateways;

import co.com.pragma.model.plato.Plato;

import java.util.Optional;

public interface PlatoRepository {
    void crearPlato(Plato plato);
    Optional<Plato> buscarPlato(Long id);
    void actualizarPlato(Plato plato);
}
