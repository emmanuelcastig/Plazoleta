package co.com.pragma.model.restaurante.consumer;

public interface PropietarioConsumerGateway {
    boolean verificarExistenciaPropietario(Long id, String token);
}
