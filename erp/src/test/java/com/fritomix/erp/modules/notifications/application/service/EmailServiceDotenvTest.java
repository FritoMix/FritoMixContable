package com.fritomix.erp.modules.notifications.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class EmailServiceDotenvTest {

    @TempDir
    Path tempDir;

    @Test
    void resuelveCredencialesDesdeElDotenvIndicado() throws IOException {
        Path dotenv = tempDir.resolve(".env");
        Files.writeString(dotenv, """
                # credenciales de prueba
                MAIL_API_KEY=test-api-key
                MAIL_FROM=noreply@fritomix.test
                """);

        EmailService service = new EmailService(mock(JavaMailSender.class), "", "");
        service.dotenvLocations = List.of(dotenv.toString());

        assertEquals("test-api-key", service.resolveFromDotenv("MAIL_API_KEY", ""));
        assertEquals("noreply@fritomix.test", service.resolveFromDotenv("MAIL_FROM", ""));
    }

    @Test
    void elValorConfiguradoTienePrioridadSobreElDotenv() throws IOException {
        Path dotenv = tempDir.resolve(".env");
        Files.writeString(dotenv, "MAIL_API_KEY=desde-dotenv\n");

        EmailService service = new EmailService(mock(JavaMailSender.class), "", "");
        service.dotenvLocations = List.of(dotenv.toString());

        assertEquals("desde-config", service.resolveFromDotenv("MAIL_API_KEY", "desde-config"));
    }

    @Test
    void devuelveNullSiNoHayDotenvNiValorConfigurado() {
        EmailService service = new EmailService(mock(JavaMailSender.class), "", "");
        service.dotenvLocations = List.of(tempDir.resolve("inexistente.env").toString());

        assertNull(service.resolveFromDotenv("MAIL_API_KEY", ""));
    }
}