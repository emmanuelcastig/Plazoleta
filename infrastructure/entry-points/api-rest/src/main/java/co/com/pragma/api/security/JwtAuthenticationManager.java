package co.com.pragma.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements AuthenticationManager {

    private final JwtProvider jwtProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String authToken = authentication.getCredentials().toString();

        if (!jwtProvider.validateToken(authToken)) {
            throw new RuntimeException("Token inválido");
        }

        Long id = jwtProvider.extractId(authToken);
        String rol = jwtProvider.extractRol(authToken);

        return new UsernamePasswordAuthenticationToken(
                id,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol))
        );
    }
}
