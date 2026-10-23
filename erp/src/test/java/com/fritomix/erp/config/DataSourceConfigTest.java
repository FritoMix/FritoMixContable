package com.fritomix.erp.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DataSourceConfigTest {

    @Test
    void usaLasCredencialesEmbebidasCuandoLasPropiedadesEstanVacias() {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl("postgresql://usuario:clave@host:5432/base");
        properties.setUsername("");
        properties.setPassword("");

        JdbcConnectionDetails details = new DataSourceConfig().jdbcConnectionDetails(properties);

        assertEquals("jdbc:postgresql://host:5432/base", details.getJdbcUrl());
        assertEquals("usuario", details.getUsername());
        assertEquals("clave", details.getPassword());
        assertEquals("org.postgresql.Driver", details.getDriverClassName());
    }

    @Test
    void prefiereLasCredencialesConfiguradasPorSeparado() {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl("postgres://usuario-url:clave-url@host:5432/base");
        properties.setUsername("usuario-configurado");
        properties.setPassword("clave-configurada");

        JdbcConnectionDetails details = new DataSourceConfig().jdbcConnectionDetails(properties);

        assertEquals("usuario-configurado", details.getUsername());
        assertEquals("clave-configurada", details.getPassword());
    }

    @Test
    void respetaElDriverExplicitoDeLasPruebas() {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl("jdbc:h2:mem:testdb");
        properties.setDriverClassName("org.h2.Driver");

        JdbcConnectionDetails details = new DataSourceConfig().jdbcConnectionDetails(properties);

        assertEquals("org.h2.Driver", details.getDriverClassName());
    }

    @Test
    void dejaQueSpringInfieraElDriverDeOtrasBases() {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl("jdbc:h2:mem:testdb");

        JdbcConnectionDetails details = new DataSourceConfig().jdbcConnectionDetails(properties);

        assertNull(details.getDriverClassName());
    }
}
