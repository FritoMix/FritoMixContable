package com.fritomix.erp.modules.notifications.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
public class EmailService {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final JavaMailSender mailSender;
    private final RestClient restClient;

    @Value("${mail.api-key:}")
    private String brevoApiKey;

    @Value("${mail.from:}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.restClient = RestClient.create();
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
}
