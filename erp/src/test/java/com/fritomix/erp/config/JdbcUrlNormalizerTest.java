package com.fritomix.erp.config;

import com.fritomix.erp.config.JdbcUrlNormalizer.NormalizedJdbcUrl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JdbcUrlNormalizerTest {

    @Test
    void dejaIntactaUnaUrlJdbc() {
        NormalizedJdbcUrl result = JdbcUrlNormalizer.normalize(
                "jdbc:postgresql://localhost:5432/fritomix_erp?sslmode=require");

        assertEquals("jdbc:postgresql://localhost:5432/fritomix_erp?sslmode=require", result.url());
        assertNull(result.username());
        assertNull(result.password());
    }

    @Test
    void convierteElEsquemaPostgres() {
        NormalizedJdbcUrl result = JdbcUrlNormalizer.normalize(
                "postgres://dpg-abc-a.oregon-postgres.render.com:5432/fritomix_erp");

        assertEquals("jdbc:postgresql://dpg-abc-a.oregon-postgres.render.com:5432/fritomix_erp",
                result.url());
        assertNull(result.username());
        assertNull(result.password());
    }

    @Test
    void convierteElEsquemaPostgresqlYExtraeCredenciales() {
        NormalizedJdbcUrl result = JdbcUrlNormalizer.normalize(
                "postgresql://usuario:clave@dpg-abc-a.oregon-postgres.render.com:5432/fritomix_erp"
                        + "?sslmode=require");

        assertEquals("jdbc:postgresql://dpg-abc-a.oregon-postgres.render.com:5432/fritomix_erp"
                + "?sslmode=require", result.url());
        assertEquals("usuario", result.username());
        assertEquals("clave", result.password());
    }

    @Test
    void decodificaCredencialesSinConvertirMasEnEspacio() {
        NormalizedJdbcUrl result = JdbcUrlNormalizer.normalize(
                "postgresql://usuario:clave%40segura+2026@host:5432/base");

        assertEquals("usuario", result.username());
        assertEquals("clave@segura+2026", result.password());
    }

    @Test
    void quitaCredencialesDeUnaUrlJdbcExistente() {
        NormalizedJdbcUrl result = JdbcUrlNormalizer.normalize(
                "jdbc:postgresql://usuario:clave@host:5432/base");

        assertEquals("jdbc:postgresql://host:5432/base", result.url());
        assertEquals("usuario", result.username());
        assertEquals("clave", result.password());
    }

    @Test
    void aceptaElEsquemaEnMayusculas() {
        NormalizedJdbcUrl result = JdbcUrlNormalizer.normalize(
                "POSTGRESQL://usuario:clave@host:5432/base");

        assertEquals("jdbc:postgresql://host:5432/base", result.url());
    }

    @Test
    void dejaIntactasOtrasUrlsJdbc() {
        NormalizedJdbcUrl result = JdbcUrlNormalizer.normalize(
                "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");

        assertEquals("jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", result.url());
    }

    @Test
    void rechazaEsquemasDesconocidosSinExponerLaClave() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> JdbcUrlNormalizer.normalize("mysql://usuario:secreto@host:3306/base"));

        assertFalse(error.getMessage().contains("secreto"));
    }

    @Test
    void rechazaUnaUrlVacia() {
        assertThrows(IllegalStateException.class, () -> JdbcUrlNormalizer.normalize("  "));
    }
}
