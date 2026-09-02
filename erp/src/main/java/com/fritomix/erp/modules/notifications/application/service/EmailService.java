package com.fritomix.erp.modules.notifications.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final JavaMailSender mailSender;
    private final RestClient restClient;

    private final String brevoApiKey;
    private final String mailFrom;

    public EmailService(JavaMailSender mailSender,
                        @Value("${mail.api-key:}") String brevoApiKey,
                        @Value("${mail.from:}") String mailFrom) {
        this.mailSender = mailSender;
        this.restClient = RestClient.create();
        this.brevoApiKey = resolveFromDotenv("MAIL_API_KEY", brevoApiKey);
        this.mailFrom = resolveFromDotenv("MAIL_FROM", mailFrom);
    }

    /**
     * Envía un correo de forma síncrona. Si el envío falla lanza una excepción
     * para que el error sea visible y no se pierda en silencio.
     */
    public void sendEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Destinatario de correo vacío");
        }
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            sendViaBrevo(to, subject, body);
        } else {
            sendViaSmtp(to, subject, body);
        }
    }

    /**
     * Variante que nunca lanza: registra el error y continúa. Útil para notificaciones
     * secundarias donde un fallo de correo no debe interrumpir la operación principal.
     */
    public void sendEmailQuietly(String to, String subject, String body) {
        try {
            sendEmail(to, subject, body);
        } catch (Exception e) {
            log.warn("No se pudo enviar email a {}: {}", to, e.getMessage());
        }
    }

    private void sendViaBrevo(String to, String subject, String body) {
        if (mailFrom == null || mailFrom.isBlank()) {
            throw new IllegalStateException(
                    "mail.from no configurado: define MAIL_FROM con un remitente verificado en Brevo");
        }
        Map<String, Object> payload = Map.of(
                "sender", Map.of("name", "FritoMix", "email", mailFrom),
                "to", java.util.List.of(Map.of("email", to)),
                "subject", subject,
                "textContent", body
        );
        String response = restClient.post()
                .uri(BREVO_API_URL)
                .header("api-key", brevoApiKey)
                .header("Accept", "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .onStatus(status -> status.isError(), (req, res) -> {
                    String detail = "";
                    try {
                        detail = new String(res.getBody().readAllBytes());
                    } catch (Exception ignored) {
                        // sin detalle adicional
                    }
                    throw new RuntimeException(
                            "Brevo respondió " + res.getStatusCode() + ": " + detail);
                })
                .body(String.class);
        log.info("Email enviado a {} via Brevo: {} (respuesta: {})", to, subject, response);
    }

    private void sendViaSmtp(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.info("Email enviado a {} via SMTP: {}", to, subject);
    }

    /**
     * Si el valor configurado está vacío, intenta leerlo del archivo .env local.
     * Así el envío por Brevo funciona en desarrollo sin depender de cómo se
     * arranque la aplicación (IDE, terminal, etc.).
     */
    String resolveFromDotenv(String envKey, String configured) {
        if (configured != null && !configured.isBlank()) {
            return configured;
        }
        String value = readDotenvValue(envKey);
        if (value != null && !value.isBlank()) {
            log.info("Credencial de correo '{}' leída del archivo .env", envKey);
        }
        return value;
    }

    private String readDotenvValue(String envKey) {
        for (String location : new String[]{".env", "erp/.env",
                Paths.get("").toAbsolutePath() + "/.env",
                Paths.get("").toAbsolutePath() + "/../.env"}) {
            Path path = Paths.get(location);
            if (!Files.exists(path)) {
                continue;
            }
            try {
                for (String line : Files.readAllLines(path)) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.startsWith(envKey + "=")) {
                        continue;
                    }
                    String value = trimmed.substring(envKey.length() + 1).trim();
                    if ((value.startsWith("\"") && value.endsWith("\""))
                            || (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    return value;
                }
            } catch (IOException e) {
                // ignorar y seguir con la siguiente ubicación
            }
        }
        return null;
    }
}
