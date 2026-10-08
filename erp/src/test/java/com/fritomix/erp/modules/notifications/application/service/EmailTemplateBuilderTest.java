package com.fritomix.erp.modules.notifications.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailTemplateBuilderTest {

    private final EmailTemplateBuilder builder = new EmailTemplateBuilder();

    @Test
    void buildNotificationIncluyeTituloMensajeYDestinatario() {
        String html = builder.buildNotification(
                "Ana Pérez", "Pedido aprobado", "Tu pedido ORD-001 fue aprobado.",
                "SUCCESS", "Ver pedido", "https://app.fritomix.test/orders/1");

        assertTrue(html.contains("Ana P"));
        assertTrue(html.contains("Pedido aprobado"));
        assertTrue(html.contains("Tu pedido ORD-001 fue aprobado."));
        assertTrue(html.contains("https://app.fritomix.test/orders/1"));
        assertTrue(html.contains("Ver pedido"));
    }

    @Test
    void buildNotificationEscapaHtmlDeEntrada() {
        String html = builder.buildNotification(
                "Bob", "<script>alert('x')</script>", "5 < 10 & 20 > 3",
                "INFO", null, null);

        assertFalse(html.contains("<script>alert"));
        assertTrue(html.contains("&lt;script&gt;"));
        assertTrue(html.contains("5 &lt; 10 &amp; 20 &gt; 3"));
    }

    @Test
    void buildNotificationSinDestinatarioUsaSaludoGenerico() {
        String html = builder.buildNotification(null, "Asunto", "Mensaje", "INFO", null, null);

        assertTrue(html.contains("Hola"));
        assertFalse(html.contains("Hola, <strong>null"));
    }

    @Test
    void buildNotificationAceptaCualquierTipoSinFallar() {
        for (String type : new String[]{"INFO", "SUCCESS", "WARNING", "ERROR", "desconocido", null}) {
            String html = builder.buildNotification("Test", "T", "M", type, null, null);
            assertTrue(html.contains("<!DOCTYPE html>"), "tipo " + type + " debe generar HTML");
        }
    }

    @Test
    void buildPasswordResetIncluyeElCodigo() {
        String html = builder.buildPasswordReset("Carlos", "483920");

        assertTrue(html.contains("483920"));
        assertTrue(html.contains("Restablecer contrase"));
    }

    @Test
    void buildPasswordResetEscapaElCodigo() {
        String html = builder.buildPasswordReset("Carlos", "<b>123</b>");

        assertFalse(html.contains("<b>123</b>"));
        assertTrue(html.contains("&lt;b&gt;123&lt;/b&gt;"));
    }

    @Test
    void buildPasswordResetSinDestinatarioUsaUsuario() {
        String html = builder.buildPasswordReset(null, "000000");

        assertTrue(html.contains("usuario"));
        assertTrue(html.contains("000000"));
    }
}