package co.com.pragma.consumer;

import co.com.pragma.model.consumer.EmpleadoConsumerGateway;
import co.com.pragma.model.consumer.PedidoConsumerGateway;
import co.com.pragma.model.consumer.PropietarioConsumerGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Slf4j
@Service
public class RestConsumer  implements PropietarioConsumerGateway, EmpleadoConsumerGateway, PedidoConsumerGateway
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
            throw new RuntimeException("Error en la comunicación con el servicio usuarios", e);
        }
    }


    @Override
    @CircuitBreaker(name = "propietarioService")
    public Long obtenerRestauranteEmpleado(Long id, String token) {
        String endpoint = url + "/api/v1/empleados/" + id + "/restaurante";

        Request request = new Request.Builder()
                .url(endpoint)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        log.info("Consultando empleado en el servicio externo: {}", endpoint);
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error al consumir servicio: " + response.code());
            }

            String body = response.body().string();
            log.info("Respuesta del servicio: {}", body);

            return Long.parseLong(body);
        } catch (IOException e) {
            throw new RuntimeException("Error en la comunicación con el servicio usuarios", e);
        }
    }

    @Override
    @CircuitBreaker(name = "propietarioService")
    public void enviarMensajeSms(String telefonoDestino, String mensaje, String token) {
        String endpoint = "http://localhost:9002/api/v1/mensajeria/enviar-sms";

        try {
            // Normalizar número: quitar espacios y agregar prefijo +
            telefonoDestino = telefonoDestino.trim();
            if (!telefonoDestino.startsWith("+")) {
                telefonoDestino = "+" + telefonoDestino;
            }

            ObjectRequest objectRequest = ObjectRequest.builder()
                    .telefonoDestino(telefonoDestino)
                    .mensaje(mensaje)
                    .build();

            String jsonBody = mapper.writeValueAsString(objectRequest);

            RequestBody body = RequestBody.create(
                    jsonBody,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(endpoint)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            log.info("Consumir servicio mensajería en: {}", endpoint);
            log.info("Payload enviado: {}", jsonBody);

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Error al consumir servicio: " + response.code() +
                            " - " + response.message());
                }
                log.info("Respuesta del servicio: {}", response.code());
            }

        } catch (IOException e) {
            throw new RuntimeException("Error en la comunicación con el servicio de mensajería", e);
        }
    }
}
