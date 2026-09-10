package com.fritomix.erp.modules.notifications.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

/**
 * Construye correos HTML con la identidad visual de FritoMix.
 * El logo se embebe en base64 para evitar dependencias externas y que
 * los clientes de correo con imágenes bloqueadas lo muestren igualmente.
 */
@Component
@Slf4j
public class EmailTemplateBuilder {

    private static final String BRAND_COLOR      = "#0055FF";
    private static final String BRAND_DARK        = "#071938";
    private static final String BRAND_ACCENT      = "#E8F0FF";
    private static final String FOOTER_COLOR      = "#6B7280";
    private static final String SUCCESS_COLOR     = "#10B981";
    private static final String WARNING_COLOR     = "#F59E0B";
    private static final String DANGER_COLOR      = "#EF4444";
    private static final String INFO_COLOR        = "#0055FF";

    private static final String LOGO_BASE64 = loadLogoBase64();

    // ─────────────────────────────────────────────────────────────────────────
    // Public factory methods
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Correo genérico de notificación del sistema.
     *
     * @param recipientName nombre del destinatario (solo para el saludo)
     * @param title         título principal de la tarjeta
     * @param message       cuerpo del mensaje
     * @param type          INFO | SUCCESS | WARNING | ERROR
     * @param ctaLabel      texto del botón CTA (puede ser null)
     * @param ctaUrl        URL del botón CTA (puede ser null)
     */
    public String buildNotification(String recipientName,
                                    String title,
                                    String message,
                                    String type,
                                    String ctaLabel,
                                    String ctaUrl) {
        String accentColor  = colorForType(type);
        String typeIcon     = iconForType(type);
        String typeBadge    = badgeForType(type);

        String cta = buildCta(ctaLabel, ctaUrl, accentColor);

        String body = """
                <div style="margin-bottom:24px;">
                  <div style="display:inline-flex;align-items:center;gap:8px;
                              background:%s15;border-left:4px solid %s;
                              padding:10px 16px;border-radius:0 8px 8px 0;margin-bottom:20px;">
                    <span style="font-size:20px;">%s</span>
                    %s
                  </div>
                </div>

                <h2 style="margin:0 0 12px;font-size:20px;font-weight:800;
                            color:%s;line-height:1.3;">%s</h2>

                <p style="margin:0 0 24px;font-size:15px;color:#374151;
                           line-height:1.7;white-space:pre-line;">%s</p>

                %s
                """.formatted(
                accentColor, accentColor, typeIcon, typeBadge,
                BRAND_DARK, escapeHtml(title),
                escapeHtml(message),
                cta
        );

        return wrap(recipientName, body);
    }

    /**
     * Correo de restablecimiento de contraseña con código grande destacado.
     */
    public String buildPasswordReset(String recipientName, String code) {
        String body = """
                <h2 style="margin:0 0 8px;font-size:20px;font-weight:800;color:%s;">
                  Restablecer contraseña
                </h2>
                <p style="margin:0 0 24px;font-size:15px;color:#374151;line-height:1.7;">
                  Hola <strong>%s</strong>, recibimos una solicitud para restablecer
                  tu contraseña. Usa el código de verificación que aparece a continuación:
                </p>

                <!-- Código destacado -->
                <div style="text-align:center;margin:28px 0;">
                  <div style="display:inline-block;background:%s;
                              border-radius:16px;padding:20px 40px;">
                    <span style="font-size:11px;font-weight:700;letter-spacing:3px;
                                  color:rgba(255,255,255,0.75);display:block;
                                  text-transform:uppercase;margin-bottom:6px;">
                      Código de verificación
                    </span>
                    <span style="font-family:'Courier New',monospace;font-size:42px;
                                  font-weight:900;letter-spacing:10px;color:#ffffff;
                                  display:block;line-height:1;">%s</span>
                  </div>
                </div>

                <!-- Info de vigencia -->
                <div style="background:#FEF3C7;border-radius:10px;padding:14px 18px;
                             margin-bottom:24px;display:flex;align-items:flex-start;gap:10px;">
                  <span style="font-size:18px;">⏱️</span>
                  <div>
                    <p style="margin:0;font-size:14px;font-weight:700;color:#92400E;">
                      Válido por 15 minutos
                    </p>
                    <p style="margin:4px 0 0;font-size:13px;color:#78350F;line-height:1.5;">
                      Si no solicitaste este cambio, puedes ignorar este correo
                      con total seguridad. Tu contraseña actual no cambiará.
                    </p>
                  </div>
                </div>

                <!-- Seguridad -->
                <div style="border-top:1px solid #E5E7EB;padding-top:16px;margin-top:8px;">
                  <p style="margin:0;font-size:13px;color:#6B7280;line-height:1.6;">
                    🔒 Por tu seguridad, nunca compartiremos este código por teléfono
                    o chat. El equipo de FritoMix <strong>jamás</strong> te lo solicitará.
                  </p>
                </div>
                """.formatted(
                BRAND_DARK,
                escapeHtml(recipientName != null ? recipientName : "usuario"),
                BRAND_COLOR,
                escapeHtml(code)
        );

        return wrap(recipientName, body);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Envuelve el contenido en la estructura HTML completa con cabecera y pie. */
    private String wrap(String recipientName, String content) {
        String greeting = recipientName != null && !recipientName.isBlank()
                ? "Hola, <strong>" + escapeHtml(recipientName) + "</strong> 👋"
                : "Hola 👋";

        String logoTag = LOGO_BASE64 != null
                ? """
                  <img src="data:image/png;base64,%s"
                       alt="FritoMix" width="140" height="auto"
                       style="display:block;max-width:140px;" />
                  """.formatted(LOGO_BASE64)
                : """
                  <span style="font-size:22px;font-weight:900;color:#ffffff;
                                letter-spacing:-1px;">FritoMix</span>
                  """;

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width,initial-scale=1"/>
                  <title>FritoMix — Notificación</title>
                </head>
                <body style="margin:0;padding:0;background-color:#F3F4F6;
                             font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',
                             Roboto,'Helvetica Neue',Arial,sans-serif;">

                  <!-- Outer wrapper -->
                  <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                         style="background:#F3F4F6;padding:40px 16px;">
                    <tr>
                      <td align="center">
                        <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                               style="max-width:600px;width:100%%;">

                          <!-- ═══ HEADER ═══ -->
                          <tr>
                            <td style="background:linear-gradient(135deg,%s 0%%,#0033AA 100%%);
                                        border-radius:20px 20px 0 0;padding:28px 32px;">
                              <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                                <tr>
                                  <td>%s</td>
                                  <td align="right"
                                      style="font-size:12px;color:rgba(255,255,255,0.7);
                                             font-weight:600;letter-spacing:1px;
                                             text-transform:uppercase;white-space:nowrap;">
                                    Sistema ERP
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>

                          <!-- ═══ GREETING BAND ═══ -->
                          <tr>
                            <td style="background:#EEF2FF;padding:14px 32px;
                                        border-left:1px solid #C7D2FE;
                                        border-right:1px solid #C7D2FE;">
                              <p style="margin:0;font-size:15px;color:#3730A3;font-weight:600;">
                                %s
                              </p>
                            </td>
                          </tr>

                          <!-- ═══ CARD BODY ═══ -->
                          <tr>
                            <td style="background:#FFFFFF;padding:32px;
                                        border-left:1px solid #E5E7EB;
                                        border-right:1px solid #E5E7EB;">
                              %s
                            </td>
                          </tr>

                          <!-- ═══ FOOTER ═══ -->
                          <tr>
                            <td style="background:#F9FAFB;border-radius:0 0 20px 20px;
                                        padding:20px 32px;border:1px solid #E5E7EB;
                                        border-top:none;">
                              <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                                <tr>
                                  <td style="font-size:12px;color:#9CA3AF;line-height:1.6;">
                                    © %d FritoMix · Sistema de Gestión Empresarial<br/>
                                    Este es un mensaje automático, por favor no respondas
                                    directamente a este correo.
                                  </td>
                                  <td align="right">
                                    <div style="width:32px;height:32px;border-radius:8px;
                                                background:%s;display:inline-flex;
                                                align-items:center;justify-content:center;
                                                font-size:16px;line-height:32px;
                                                text-align:center;">🍟</div>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>

                        </table>
                      </td>
                    </tr>
                  </table>

                </body>
                </html>
                """.formatted(
                BRAND_COLOR,
                logoTag,
                greeting,
                content,
                java.time.Year.now().getValue(),
                BRAND_COLOR
        );
    }

    private String buildCta(String label, String url, String color) {
        if (label == null || label.isBlank() || url == null || url.isBlank()) {
            return "";
        }
        return """
               <div style="text-align:center;margin:8px 0 28px;">
                 <a href="%s"
                    style="display:inline-block;background:%s;color:#ffffff;
                           font-weight:800;font-size:14px;text-decoration:none;
                           padding:14px 32px;border-radius:12px;
                           box-shadow:0 4px 14px %s40;letter-spacing:0.3px;">
                   %s &rarr;
                 </a>
               </div>
               """.formatted(url, color, color, escapeHtml(label));
    }

    private String colorForType(String type) {
        if (type == null) return INFO_COLOR;
        return switch (type.toUpperCase()) {
            case "SUCCESS" -> SUCCESS_COLOR;
            case "WARNING" -> WARNING_COLOR;
            case "ERROR"   -> DANGER_COLOR;
            default        -> INFO_COLOR;
        };
    }

    private String iconForType(String type) {
        if (type == null) return "🔔";
        return switch (type.toUpperCase()) {
            case "SUCCESS" -> "✅";
            case "WARNING" -> "⚠️";
            case "ERROR"   -> "🚨";
            default        -> "🔔";
        };
    }

    private String badgeForType(String type) {
        String label = type != null ? type : "INFO";
        String color = colorForType(type);
        return """
               <span style="font-size:11px;font-weight:700;letter-spacing:1px;
                            color:%s;text-transform:uppercase;">%s</span>
               """.formatted(color, escapeHtml(label));
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }

    private static String loadLogoBase64() {
        try {
            ClassPathResource res = new ClassPathResource("static/logo-fritomix.png");
            byte[] bytes = res.getInputStream().readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            log.warn("No se pudo cargar el logo para los correos: {}", e.getMessage());
            return null;
        }
    }
}
