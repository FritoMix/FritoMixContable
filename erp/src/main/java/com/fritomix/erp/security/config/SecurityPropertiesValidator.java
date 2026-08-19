package com.fritomix.erp.security.config;

import com.fritomix.erp.security.jwt.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

/**
 * Valida en el arranque que la aplicación no quede en producción
 * con secretos débiles o por defecto. Falla rápido en lugar de
 * arrancar una app insegura.
 */
@Configuration
public class SecurityPropertiesValidator {

    private static final String WEAK_JWT_SECRET = "ZGV2LW9ubHktc2VjcmV0LW5vdC12YWxpZC1mb3ItcHJvZA==";
    private static final String WEAK_DB_PASSWORD = "123456";

    private final JwtProperties jwtProperties;
    private final String dbPassword;
    private final String activeProfiles;

    public SecurityPropertiesValidator(
            JwtProperties jwtProperties,
            @Value("${spring.datasource.password}") String dbPassword,
            @Value("${spring.profiles.active:}") String activeProfiles) {
        this.jwtProperties = jwtProperties;
        this.dbPassword = dbPassword;
        this.activeProfiles = activeProfiles;
    }

    @PostConstruct
    public void validate() {
        if (!StringUtils.hasText(activeProfiles) || !activeProfiles.contains("prod")) {
            return;
        }
        if (WEAK_JWT_SECRET.equals(jwtProperties.getSecret())) {
            throw new IllegalStateException(
                    "Arranque abortado: JWT_SECRET no fue configurado. " +
                    "En producción se requiere un secreto JWT generado con: " +
                    "openssl rand -base64 64");
        }
        if (WEAK_DB_PASSWORD.equals(dbPassword)) {
            throw new IllegalStateException(
                    "Arranque abortado: DB_PASSWORD no fue configurado correctamente en producción.");
        }
    }
}
