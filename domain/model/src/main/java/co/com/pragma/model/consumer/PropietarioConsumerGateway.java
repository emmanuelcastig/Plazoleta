package co.com.pragma.model.consumer;

public interface PropietarioConsumerGateway {
    boolean verificarExistenciaPropietario(Long id, String token);
}
