package com.fritomix.erp.modules.notifications.application.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Envía un correo con contenido HTML.
     * Si el envío falla lanza una excepción para que el error sea visible.
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Destinatario de correo vacío");
        }
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            sendHtmlViaBrevo(to, subject, htmlBody);
        } else {
            sendHtmlViaSmtp(to, subject, htmlBody);
        }
    }

    /**
     * Variante silenciosa de {@link #sendHtmlEmail}: registra el error y continúa.
     * Útil para notificaciones secundarias donde un fallo de correo no debe
     * interrumpir la operación principal.
     */
    public void sendHtmlEmailQuietly(String to, String subject, String htmlBody) {
        try {
            sendHtmlEmail(to, subject, htmlBody);
        } catch (Exception e) {
            log.warn("No se pudo enviar email HTML a {}: {}", to, e.getMessage());
        }
    }

    /**
     * Envía un correo de texto plano (compatibilidad con código legado).
     */
    public void sendEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Destinatario de correo vacío");
        }
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            sendTextViaBrevo(to, subject, body);
        } else {
            sendTextViaSmtp(to, subject, body);
        }
    }

    /** Variante silenciosa de {@link #sendEmail}. */
    public void sendEmailQuietly(String to, String subject, String body) {
        try {
            sendEmail(to, subject, body);
        } catch (Exception e) {
            log.warn("No se pudo enviar email a {}: {}", to, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Brevo
    // ─────────────────────────────────────────────────────────────────────────

    private void sendHtmlViaBrevo(String to, String subject, String htmlBody) {
        requireMailFrom();
        Map<String, Object> payload = Map.of(
                "sender",      Map.of("name", "FritoMix", "email", mailFrom),
                "to",          java.util.List.of(Map.of("email", to)),
                "subject",     subject,
                "htmlContent", htmlBody
        );
        String response = postToBrevo(payload);
        log.info("Email HTML enviado a {} via Brevo: {} (respuesta: {})", to, subject, response);
    }

    private void sendTextViaBrevo(String to, String subject, String body) {
        requireMailFrom();
        Map<String, Object> payload = Map.of(
                "sender",      Map.of("name", "FritoMix", "email", mailFrom),
                "to",          java.util.List.of(Map.of("email", to)),
                "subject",     subject,
                "textContent", body
        );
        String response = postToBrevo(payload);
        log.info("Email texto enviado a {} via Brevo: {} (respuesta: {})", to, subject, response);
    }

    private String postToBrevo(Map<String, Object> payload) {
        return restClient.post()
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
                    }
                    throw new RuntimeException(
                            "Brevo respondió " + res.getStatusCode() + ": " + detail);
                })
                .body(String.class);
    }

    private void requireMailFrom() {
        if (mailFrom == null || mailFrom.isBlank()) {
            throw new IllegalStateException(
                    "mail.from no configurado: define MAIL_FROM con un remitente verificado en Brevo");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SMTP
    // ─────────────────────────────────────────────────────────────────────────

    private void sendHtmlViaSmtp(String to, String subject, String htmlBody) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML
            mailSender.send(mime);
            log.info("Email HTML enviado a {} via SMTP: {}", to, subject);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar email HTML via SMTP", e);
        }
    }

    private void sendTextViaSmtp(String to, String subject, String body) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);
            mailSender.send(mime);
            log.info("Email texto enviado a {} via SMTP: {}", to, subject);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar email texto via SMTP", e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // .env resolver
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Si el valor configurado está vacío, intenta leerlo del archivo .env local.
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
