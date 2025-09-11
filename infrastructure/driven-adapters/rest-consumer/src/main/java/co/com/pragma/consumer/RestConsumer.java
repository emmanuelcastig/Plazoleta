package co.com.pragma.consumer;

import co.com.pragma.model.restaurante.consumer.PropietarioConsumerGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Slf4j
@Service
public class RestConsumer  implements PropietarioConsumerGateway
{
    private final String url;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public RestConsumer(@Value("${adapter.restconsumer.url}") String url, OkHttpClient client, ObjectMapper mapper) {
        this.url = url;
        this.client = client;
        this.mapper = mapper;
    }


    @Override
    @CircuitBreaker(name = "propietarioService")
    public boolean verificarExistenciaPropietario(Long id, String token) {
        String endpoint = url + "/api/v1/propietarios/" + id;

        Request request = new Request.Builder()
                .url(endpoint)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        log.info("Consultando propietario en el servicio externo: {}", endpoint);
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error al consumir servicio: " + response.code());
            }

            String body = response.body().string();
            log.info("El propietario existe con éxito");
            return Boolean.parseBoolean(body);
        } catch (IOException e) {
            throw new RuntimeException("Error en la comunicación con el servicio propietario", e);
        }
    }
}
