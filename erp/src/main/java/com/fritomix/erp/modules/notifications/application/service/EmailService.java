package com.fritomix.erp.modules.notifications.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    public void sendEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            log.warn("No se pudo enviar email: destinatario vacío");
            return;
        }
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            sendViaBrevo(to, subject, body);
        } else {
            sendViaSmtp(to, subject, body);
        }
    }

    private void sendViaBrevo(String to, String subject, String body) {
        try {
            if (mailFrom == null || mailFrom.isBlank()) {
                log.warn("No se pudo enviar email a {}: falta mail.from (remitente verificado en Brevo)", to);
                return;
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
                        throw new RuntimeException("Brevo respondió " + res.getStatusCode());
                    })
                    .body(String.class);
            log.info("Email enviado a {} via Brevo: {}", to, subject);
        } catch (Exception e) {
            log.warn("No se pudo enviar email a {} via Brevo: {}", to, e.getMessage());
        }
    }

    private void sendViaSmtp(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email enviado a {}: {}", to, subject);
        } catch (Exception e) {
            log.warn("No se pudo enviar email a {}: {}", to, e.getMessage());
        }
    }
}