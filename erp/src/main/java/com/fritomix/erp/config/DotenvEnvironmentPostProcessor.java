package com.fritomix.erp.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Carga las variables definidas en el archivo .env (raíz del módulo) y las
 * expone en el entorno de Spring. Así las variables de correo (y de base de
 * datos) funcionan sin importar cómo se arranque la app (IDE, terminal, etc.).
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String LOCATIONS = "dotenv.locations";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, String> vars = new LinkedHashMap<>();

        for (String location : resolveLocations(environment)) {
            loadFrom(location, vars);
        }

        if (vars.isEmpty()) {
            return;
        }

        MapPropertySource source = new MapPropertySource("dotenv", new LinkedHashMap<>(vars));
        MutablePropertySources sources = environment.getPropertySources();
        if (sources.contains("dotenv")) {
            sources.remove("dotenv");
        }
        sources.addLast(source);
    }

    private String[] resolveLocations(ConfigurableEnvironment environment) {
        String configured = environment.getProperty(LOCATIONS);
        if (configured != null && !configured.isBlank()) {
            return configured.split(",");
        }
        Path classesDir = Paths.get("").toAbsolutePath().normalize();
        return new String[]{
            ".env",
            "erp/.env",
            classesDir + "/.env",
            classesDir + "/../.env"
        };
    }

    private void loadFrom(String location, Map<String, String> vars) {
        Path path = Paths.get(location);
        if (!Files.exists(path)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int idx = trimmed.indexOf('=');
                if (idx <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, idx).trim();
                String value = trimmed.substring(idx + 1).trim();
                if (key.isEmpty()) {
                    continue;
                }
                if ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }
                if (!vars.containsKey(key)) {
                    vars.put(key, value);
                }
            }
        } catch (IOException e) {
            // ignorar: el archivo es opcional
        }
    }
}
