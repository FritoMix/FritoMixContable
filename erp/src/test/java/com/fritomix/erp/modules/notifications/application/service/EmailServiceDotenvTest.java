package com.fritomix.erp.modules.notifications.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class EmailServiceDotenvTest {

    @Test
    void resolutesBrevoCredentialsFromDotenvWhenNotConfigured() {
        EmailService service = new EmailService(mock(JavaMailSender.class), "", "");

        String apiKey = service.resolveFromDotenv("MAIL_API_KEY", "");
        String from = service.resolveFromDotenv("MAIL_FROM", "");

        assertNotNull(apiKey, "MAIL_API_KEY debe resolverse desde .env");
        assertFalse(apiKey.isBlank(), "MAIL_API_KEY no debe estar vacía");
        assertNotNull(from, "MAIL_FROM debe resolverse desde .env");
        assertFalse(from.isBlank(), "MAIL_FROM no debe estar vacía");
    }
}
