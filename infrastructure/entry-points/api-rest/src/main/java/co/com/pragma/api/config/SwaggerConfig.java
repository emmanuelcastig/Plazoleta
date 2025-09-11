package co.com.pragma.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de gestión de Plazoleta")
                        .version("1.0.0")
                        .description("Documentación de la API de gestión de Plazoleta")
                        .contact(new Contact().email("emmanuelcastig@gmail.com")
                                .name("Emanuel Castillo G")));
    }
}