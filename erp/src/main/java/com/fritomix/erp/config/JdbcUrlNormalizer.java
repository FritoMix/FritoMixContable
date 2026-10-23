package com.fritomix.erp.config;

import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Adapta las URLs de PostgreSQL que entregan los proveedores cloud al formato
 * que exige el driver JDBC.
 *
 * <p>Render entrega cadenas como {@code postgres://usuario:clave@host:5432/base}
 * o {@code postgresql://...}, mientras que el driver de PostgreSQL solo acepta
 * URLs que empiecen por {@code jdbc:postgresql://}. Si la cadena incluye
 * credenciales, se separan para no dejarlas embebidas en la URL JDBC.
 */
final class JdbcUrlNormalizer {

    private static final String JDBC_POSTGRESQL_PREFIX = "jdbc:postgresql://";
    private static final String POSTGRES_SCHEME = "postgres";
    private static final String POSTGRESQL_SCHEME = "postgresql";

    private JdbcUrlNormalizer() {
    }

    static NormalizedJdbcUrl normalize(String rawUrl) {
        if (!StringUtils.hasText(rawUrl)) {
            throw new IllegalStateException(
                    "La URL de la base de datos no está configurada "
                            + "(DB_URL, DATABASE_URL o SPRING_DATASOURCE_URL)");
        }

        String url = rawUrl.trim();
        String address;

        if (url.startsWith(JDBC_POSTGRESQL_PREFIX)) {
            address = url.substring(JDBC_POSTGRESQL_PREFIX.length());
        } else if (url.startsWith("jdbc:")) {
            // Otras bases JDBC (p. ej. H2 en pruebas) ya están en el formato correcto.
            return new NormalizedJdbcUrl(url, null, null);
        } else {
            int schemeSeparator = url.indexOf("://");
            String scheme = schemeSeparator > 0
                    ? url.substring(0, schemeSeparator).toLowerCase(Locale.ROOT)
                    : "";
            if (!POSTGRES_SCHEME.equals(scheme) && !POSTGRESQL_SCHEME.equals(scheme)) {
                throw new IllegalArgumentException(
                        "La URL de la base de datos debe usar "
                                + "jdbc:postgresql://, postgres:// o postgresql://");
            }
            address = url.substring(schemeSeparator + 3);
        }

        String userInfo = null;
        int at = address.lastIndexOf('@');
        if (at >= 0) {
            userInfo = address.substring(0, at);
            address = address.substring(at + 1);
        }

        String username = null;
        String password = null;
        if (userInfo != null) {
            int colon = userInfo.indexOf(':');
            if (colon >= 0) {
                username = decode(userInfo.substring(0, colon));
                password = decode(userInfo.substring(colon + 1));
            } else if (!userInfo.isEmpty()) {
                username = decode(userInfo);
            }
        }

        return new NormalizedJdbcUrl(JDBC_POSTGRESQL_PREFIX + address, username, password);
    }

    private static String decode(String value) {
        // UriUtils no convierte '+' en espacio, a diferencia de URLDecoder.
        return UriUtils.decode(value, StandardCharsets.UTF_8);
    }

    /**
     * URL lista para JDBC y las credenciales que venían embebidas, si las había.
     */
    record NormalizedJdbcUrl(String url, String username, String password) {
    }
}
