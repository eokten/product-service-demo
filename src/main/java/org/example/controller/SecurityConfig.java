package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Base64;
import java.util.Map;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    @Value("${auth.issuers.google}")
    private String googleIssuer;

    @Value("${auth.issuers.microsoft}")
    private String microsoftIssuer;

    private Map<String, JwtDecoder> decoders;

    @PostConstruct
    public void init() {
        decoders = Map.of(
                googleIssuer, JwtDecoders.fromIssuerLocation(googleIssuer),
                microsoftIssuer, JwtDecoders.fromIssuerLocation(microsoftIssuer)
        );
    }

    @Bean
    public JwtDecoder multiTenancyJwtDecoder() {
        return token -> {
            String[] parts = token.split("\\.");

            if (parts.length <= 2) {
                throw new JwtException("Invalid JWT");
            }

            String payload = new String(Base64.getDecoder().decode(parts[1]));

            try {
                Map<String, Object> claims = objectMapper.readValue(payload, new TypeReference<>() {
                });
                String issuer = String.valueOf(claims.get("iss"));

                JwtDecoder jwtDecoder = decoders.get(issuer);

                if (jwtDecoder == null) {
                    throw new JwtException("Unable to decode JWT. Issuer not found or not supported");
                }

                return jwtDecoder.decode(token);
            } catch (JsonProcessingException e) {
                throw new JwtException("Invalid JWT payload", e);
            }
        };
    }

    // Access-Control-Allow-Origin:

    @SneakyThrows
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(CorsConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .oauth2ResourceServer(oauth2Customizer -> oauth2Customizer.jwt(jwtConfigurer -> jwtConfigurer.decoder(multiTenancyJwtDecoder())))
                .build();
    }
}
