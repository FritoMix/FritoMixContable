package com.fritomix.erp.modules.reports.api;

import com.fritomix.erp.modules.notifications.application.dto.request.NotificationRequest;
import com.fritomix.erp.modules.notifications.application.service.NotificationService;
import com.fritomix.erp.modules.reports.application.ReportsService;
import com.fritomix.erp.security.service.CustomUserDetails;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportsController {

    private static final java.util.Set<String> VALID_TYPES = java.util.Set.of("ventas", "clientes", "productos");
    private static final java.util.Set<String> VALID_PERIODS = java.util.Set.of("hoy", "semana", "mes", "trimestre", "ano");

    private final ReportsService reportsService;
    private final NotificationService notificationService;

    @GetMapping("/kpis")
    @PreAuthorize("hasAuthority('PERMISSION_REPORTS_VIEW')")
    public ResponseEntity<ReportsDTO.KPIResponse> getKPIs(@RequestParam(defaultValue = "mes") String period) {
        if (!VALID_PERIODS.contains(period)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(reportsService.getKPIs(period));
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasAuthority('PERMISSION_REPORTS_VIEW')")
    public ResponseEntity<List<ReportsDTO.TopProduct>> getTopProducts(@RequestParam(defaultValue = "mes") String period) {
        if (!VALID_PERIODS.contains(period)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(reportsService.getTopProducts(period));
    }

    @GetMapping("/top-clients")
    @PreAuthorize("hasAuthority('PERMISSION_REPORTS_VIEW')")
    public ResponseEntity<List<ReportsDTO.TopClient>> getTopClients(@RequestParam(defaultValue = "mes") String period) {
        if (!VALID_PERIODS.contains(period)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(reportsService.getTopClients(period));
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('PERMISSION_REPORTS_VIEW')")
    public void downloadPdf(@RequestParam(defaultValue = "ventas") String type,
                            @RequestParam(defaultValue = "mes") String period,
                            HttpServletResponse response) throws Exception {
        if (!VALID_TYPES.contains(type) || !VALID_PERIODS.contains(period)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.getWriter().write("Tipo '" + type + "' o período '" + period + "' inválido. Tipos válidos: " + VALID_TYPES + ". Períodos: " + VALID_PERIODS + ".");
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        String periodLabel = switch (period) {
            case "hoy" -> "Hoy";
            case "semana" -> "Semana actual";
            case "mes" -> "Mes actual";
            case "trimestre" -> "Trimestre";
            case "ano" -> "Año";
            default -> period;
        };

        document.add(new Paragraph("FRITOMIX S.A.S.", titleFont));
        document.add(new Paragraph("Reporte de " + switch (type) {
            case "ventas" -> "Ventas";
            case "clientes" -> "Clientes";
            case "productos" -> "Productos";
            default -> type;
        } + " — Periodo: " + periodLabel, subtitleFont));
        document.add(new Paragraph("Generado: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), subtitleFont));
        document.add(Chunk.NEWLINE);

        if ("clientes".equals(type)) {
            renderClientReport(document, period, headerFont, cellFont);
        } else {
            renderProductReport(document, period, headerFont, cellFont);
        }

        document.close();

        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                Long userId = userDetails.getUser().getId();
                notificationService.create(NotificationRequest.builder()
                        .userId(userId)
                        .title("Reporte generado")
                        .message("Se generó el reporte PDF de " + type + " - período " + periodLabel + ".")
                        .type("INFO")
                        .build());
            }
        } catch (Exception ignored) {}

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-" + type + "-" + period + ".pdf");
        response.getOutputStream().write(baos.toByteArray());
        response.getOutputStream().flush();
    }

    private void renderProductReport(Document document, String period, Font headerFont, Font cellFont) throws DocumentException {
        List<ReportsDTO.TopProduct> products = reportsService.getTopProducts(period);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.5f, 3, 2, 1.5f, 2});

        Color headerBg = new Color(7, 25, 56);
        String[] headers = {"#", "Producto", "Código", "Unidades", "Monto"};
        for (String h : headers) {
            var cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setPadding(6);
            table.addCell(cell);
        }

        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("es", "CO"));
        NumberFormat moneyFmt = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        for (ReportsDTO.TopProduct p : products) {
            table.addCell(new Phrase(String.valueOf(p.rank()), cellFont));
            table.addCell(new Phrase(p.name(), cellFont));
            table.addCell(new Phrase(p.code(), cellFont));
            table.addCell(new Phrase(fmt.format(p.units()), cellFont));
            table.addCell(new Phrase(moneyFmt.format(p.amount()), cellFont));
        }

        document.add(table);
    }

    private void renderClientReport(Document document, String period, Font headerFont, Font cellFont) throws DocumentException {
        List<ReportsDTO.TopClient> clients = reportsService.getTopClients(period);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1.5f, 2});

        Color headerBg = new Color(7, 25, 56);
        String[] headers = {"Cliente", "Pedidos", "Fecha del Pedido"};
        for (String h : headers) {
            var cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setPadding(6);
            table.addCell(cell);
        }

        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("es", "CO"));
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (ReportsDTO.TopClient c : clients) {
            table.addCell(new Phrase(c.name(), cellFont));
            table.addCell(new Phrase(fmt.format(c.orders()), cellFont));
            table.addCell(new Phrase(c.lastOrderDate() != null ? c.lastOrderDate().format(dateFmt) : "—", cellFont));
        }

        document.add(table);
    }
}
