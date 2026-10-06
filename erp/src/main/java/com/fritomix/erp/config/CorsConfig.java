package com.fritomix.erp.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    /**
     * Rechaza en el arranque configuraciones CORS inseguras:
     * con allowCredentials=true nunca se debe permitir un wildcard
     * (p. ej. {@code https://*.vercel.app}), porque un atacante bajo
     * ese dominio podría leer respuestas con credenciales.
     */
    @PostConstruct
    public void validate() {
        List<String> origins = splitOrigins();
        if (!allowCredentials() || !origins.stream().anyMatch(o -> o.contains("*"))) {
            return;
        }
        throw new IllegalStateException(
                "Arranque abortado: CORS con allowCredentials=true no puede combinar wildcards " +
                "en app.cors.allowed-origins=" + allowedOrigins + ". " +
                "Usa orígenes exactos como https://fritomix.com."
        );
    }

    private boolean allowCredentials() {
        return true;
    }

    private List<String> splitOrigins() {
        List<String> origins = new ArrayList<>();
        for (String origin : allowedOrigins.split(",")) {
            String trimmed = origin.trim();
            if (!trimmed.isEmpty()) {
                origins.add(trimmed);
            }
        }
        return origins;
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(splitOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}