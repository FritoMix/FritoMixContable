package com.fritomix.erp.security.config;

import com.fritomix.erp.security.jwt.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

import java.util.Set;

/**
 * Valida en el arranque que la aplicación no quede en producción
 * con secretos débiles o por defecto. Falla rápido en lugar de
 * arrancar una app insegura.
 */
@Configuration
public class SecurityPropertiesValidator {

    private static final String WEAK_JWT_SECRET = "ZGV2LW9ubHktc2VjcmV0LW5vdC12YWxpZC1mb3ItcHJvZA==";
    private static final String UNCONFIGURED_JWT_SECRET = "CHANGE_ME_IN_PRODUCTION";

    /**
     * Passwords that must never reach a production database. The placeholder is included
     * because docker-compose falls back to it when {@code .env} is missing, which would
     * otherwise start the application against a database protected by a public constant.
     */
    private static final Set<String> WEAK_DB_PASSWORDS = Set.of(
            "123456",
            "postgres",
            "admin",
            "CHANGE_ME_IN_PRODUCTION");

    private final JwtProperties jwtProperties;
    private final String dbPassword;
    private final String flywayPassword;
    private final String activeProfiles;

    public SecurityPropertiesValidator(
            JwtProperties jwtProperties,
            @Value("${spring.datasource.password}") String dbPassword,
            @Value("${spring.flyway.password:${spring.datasource.password}}") String flywayPassword,
            @Value("${spring.profiles.active:}") String activeProfiles) {
        this.jwtProperties = jwtProperties;
        this.dbPassword = dbPassword;
        this.flywayPassword = flywayPassword;
        this.activeProfiles = activeProfiles;
    }

    @PostConstruct
    public void validate() {
        if (!StringUtils.hasText(jwtProperties.getSecret())) {
            throw new IllegalStateException(
                    "Arranque abortado: JWT_SECRET no fue configurado. " +
                    "Genera uno con: openssl rand -base64 64");
        }
        if (WEAK_JWT_SECRET.equals(jwtProperties.getSecret())
                || UNCONFIGURED_JWT_SECRET.equals(jwtProperties.getSecret())) {
            throw new IllegalStateException(
                    "Arranque abortado: JWT_SECRET no fue configurado. " +
                    "En producción se requiere un secreto JWT generado con: " +
                    "openssl rand -base64 64");
        }
        if (!StringUtils.hasText(activeProfiles) || !activeProfiles.contains("prod")) {
            return;
        }
        requireStrongPassword("spring.datasource.password", dbPassword);
        requireStrongPassword("spring.flyway.password", flywayPassword);
    }

    private void requireStrongPassword(String property, String password) {
        if (!StringUtils.hasText(password)) {
            throw new IllegalStateException(
                    "Arranque abortado: " + property + " no fue configurado en producción.");
        }
        if (WEAK_DB_PASSWORDS.contains(password)) {
            throw new IllegalStateException(
                    "Arranque abortado: " + property +
                    " usa un valor por defecto. Genera uno con: openssl rand -base64 24");
        }
    }
}
