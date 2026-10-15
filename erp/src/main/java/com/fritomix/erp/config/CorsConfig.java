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
     * Rechaza en el arranque configuraciones CORS inseguras: con allowCredentials=true
     * no se admite un wildcard amplio (p. ej. {@code *} o {@code https://*}), porque un
     * atacante bajo cualquier origen leería respuestas con credenciales.
     *
     * <p>Los patrones de subdominio acotados (p. ej. {@code https://*.vercel.app}) sí se
     * permiten, porque solo abren ese dominio y se resuelven con
     * {@code setAllowedOriginPatterns}, que soporta wildcard junto a credenciales.
     */
    @PostConstruct
    public void validate() {
        if (!allowCredentials()) {
            return;
        }
        for (String origin : splitOrigins()) {
            if (origin.contains("*") && !isSafeSubdomainPattern(origin)) {
                throw new IllegalStateException(
                        "Arranque abortado: CORS con allowCredentials=true no admite el patrón '" + origin + "'. " +
                        "Usa orígenes exactos (https://fritomix.com) o un patrón de subdominio acotado " +
                        "(https://*.vercel.app)."
                );
            }
        }
    }

    private boolean allowCredentials() {
        return true;
    }

    /** {@code http(s)://*.dominio.tld}: wildcard solo en el primer label del host. */
    private boolean isSafeSubdomainPattern(String origin) {
        return origin.matches("https?://\\*\\.[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
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
        config.setAllowedOriginPatterns(splitOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}