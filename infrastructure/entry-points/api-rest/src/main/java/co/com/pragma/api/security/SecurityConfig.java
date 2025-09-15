package co.com.pragma.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers("/api/v1/plazoleta/crear/restaurante").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/v1/plazoleta/crear/plato").hasRole("PROPIETARIO")
                        .requestMatchers("/api/v1/plazoleta/actualizar/plato/*").hasRole("PROPIETARIO")
                        .requestMatchers("/api/v1/plazoleta/restaurantes").hasRole("CLIENTE")
                        .requestMatchers("/api/v1/plazoleta/platos").hasRole("CLIENTE")
                        .requestMatchers("/api/v1/plazoleta/pedidos").hasRole("CLIENTE")
                        .requestMatchers("/api/v1/plazoleta/pedidos/listar").hasRole("EMPLEADO")
                        .requestMatchers("/api/v1/plazoleta/pedidos/actualizar").hasRole("EMPLEADO")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public JwtAuthenticationManager jwtAuthenticationManager(JwtProvider jwtProvider) {
        return new JwtAuthenticationManager(jwtProvider);
    }


}
