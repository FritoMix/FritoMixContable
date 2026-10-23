package com.fritomix.erp.config;

import com.fritomix.erp.config.JdbcUrlNormalizer.NormalizedJdbcUrl;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Define las credenciales que Spring Boot usa para construir el {@code DataSource}.
 *
 * <p>El perfil {@code prod} recibe la URL desde una variable de entorno del
 * proveedor cloud, que puede venir como {@code postgres://...} o
 * {@code postgresql://...}. Aquí se normaliza a {@code jdbc:postgresql://...}
 * antes de que HikariCP la valide y se resuelven las credenciales embebidas.
 */
@Configuration(proxyBeanMethods = false)
public class DataSourceConfig {

    private static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";

    @Bean
    JdbcConnectionDetails jdbcConnectionDetails(DataSourceProperties properties) {
        return new NormalizedJdbcConnectionDetails(properties);
    }

    static final class NormalizedJdbcConnectionDetails implements JdbcConnectionDetails {

        private final String jdbcUrl;
        private final String username;
        private final String password;
        private final String driverClassName;

        NormalizedJdbcConnectionDetails(DataSourceProperties properties) {
            NormalizedJdbcUrl normalized = JdbcUrlNormalizer.normalize(properties.getUrl());
            this.jdbcUrl = normalized.url();
            this.username = firstNonBlank(properties.getUsername(), normalized.username());
            this.password = firstNonBlank(properties.getPassword(), normalized.password());
            this.driverClassName = StringUtils.hasText(properties.getDriverClassName())
                    ? properties.getDriverClassName()
                    : (jdbcUrl.startsWith("jdbc:postgresql:") ? POSTGRESQL_DRIVER : null);
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getJdbcUrl() {
            return jdbcUrl;
        }

        @Override
        public String getDriverClassName() {
            return driverClassName;
        }

        private static String firstNonBlank(String preferred, String fallback) {
            return StringUtils.hasText(preferred) ? preferred : fallback;
        }
    }
}
