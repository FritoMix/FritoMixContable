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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportsController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Color HEADER_BG = new Color(7, 25, 56);
    private static final List<String> VALID_STATUSES =
            List.of("APROBADO", "PENDIENTE", "CANCELADO");

    private final ReportsService reportsService;
    private final NotificationService notificationService;

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('PERMISSION_REPORTS_VIEW')")
    public ResponseEntity<?> getOrders(@RequestParam String status) {
        if (!VALID_STATUSES.contains(status)) {
            return ResponseEntity.badRequest().body("Estado inválido. Valores válidos: " + VALID_STATUSES);
        }
        return ResponseEntity.ok(reportsService.getOrdersByStatus(status));
    }

    @GetMapping("/logistica")
    @PreAuthorize("hasAuthority('PERMISSION_REPORTS_VIEW')")
    public ResponseEntity<List<ReportsDTO.DispatchReportDTO>> getLogistica() {
        return ResponseEntity.ok(reportsService.getDespachados());
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('PERMISSION_REPORTS_VIEW')")
    public void downloadPdf(@RequestParam(defaultValue = "logistica") String type,
                            HttpServletResponse response) throws Exception {
        String reportType = switch (type) {
            case "aprobados" -> "APROBADO";
            case "pendientes" -> "PENDIENTE";
            case "cancelados" -> "CANCELADO";
            case "logistica" -> "LOGISTICA";
            default -> null;
        };

        if (reportType == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.getWriter().write("Tipo inválido. Valores válidos: aprobados, pendientes, cancelados, logistica.");
            return;
        }

        List<ReportsDTO.OrderReportDTO> rows;
        if ("LOGISTICA".equals(reportType)) {
            rows = null;
        } else {
            rows = reportsService.getOrdersByStatus(reportType);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        addHeader(document, reportType);
        if ("LOGISTICA".equals(reportType)) {
            renderDispatchTable(document, reportsService.getDespachados());
        } else {
            renderTable(document, rows);
        }
        document.addTitle("Reporte - " + reportType);
        document.close();

        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                notificationService.create(NotificationRequest.builder()
                        .userId(userDetails.getUser().getId())
                        .title("Reporte generado")
                        .message("Se generó el reporte " + type + " en PDF.")
                        .type("INFO")
                        .link("/reportes")
                        .build());
            }
        } catch (Exception ignored) {}

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-" + type + ".pdf");
        response.getOutputStream().write(baos.toByteArray());
        response.getOutputStream().flush();
    }

    private void addHeader(Document document, String reportType) throws Exception {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{1, 3});

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_CENTER);
        try {
            ClassPathResource res = new ClassPathResource("static/logo-fritomix.png");
            byte[] imgBytes = res.getInputStream().readAllBytes();
            Image logo = Image.getInstance(imgBytes);
            logo.scaleToFit(70, 70);
            logoCell.addElement(logo);
        } catch (Exception ignored) {
            logoCell.setPhrase(new Paragraph(" "));
        }

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, HEADER_BG);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, new Color(90, 90, 90));
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(130, 130, 130));

        String title = switch (reportType) {
            case "APROBADO" -> "Reporte de Pedidos Aprobados";
            case "PENDIENTE" -> "Reporte de Pedidos Pendientes";
            case "CANCELADO" -> "Reporte de Pedidos Cancelados";
            default -> "Reporte de Logística — Despachos Realizados";
        };

        titleCell.addElement(new Paragraph("FRITOMIX S.A.S.", companyFont));
        titleCell.addElement(new Paragraph(title, subtitleFont));
        titleCell.addElement(new Paragraph("Fecha de descarga: " + LocalDateTime.now().format(DATETIME_FMT), dateFont));

        header.addCell(logoCell);
        header.addCell(titleCell);
        document.add(header);

        Paragraph separator = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 1));
        separator.setSpacingAfter(8);
        document.add(separator);
    }

    private void renderTable(Document document, List<ReportsDTO.OrderReportDTO> rows)
            throws DocumentException {
        String[] headers = new String[]{"Pedido", "Cliente", "Ciudad", "Fecha", "Peso (kg)", "Estado"};
        float[] widths = new float[]{2, 3, 1.8f, 1.8f, 1.6f, 1.8f};

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setWidths(widths);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(HEADER_BG);
            cell.setPadding(6);
            cell.setHorizontalAlignment("Peso (kg)".equals(h) ? Element.ALIGN_RIGHT : Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
        Font boldCellFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);

        boolean zebra = false;
        for (ReportsDTO.OrderReportDTO r : rows) {
            Color bg = zebra ? new Color(243, 245, 250) : Color.WHITE;
            zebra = !zebra;

            table.addCell(styledCell(r.orderNumber(), boldCellFont, bg, Element.ALIGN_LEFT));
            table.addCell(styledCell(r.customerName() != null ? r.customerName() : "—", cellFont, bg, Element.ALIGN_LEFT));
            table.addCell(styledCell(r.city() != null ? r.city() : "—", cellFont, bg, Element.ALIGN_LEFT));
            table.addCell(styledCell(r.orderDate() != null ? r.orderDate().format(DATE_FMT) : "—", cellFont, bg, Element.ALIGN_CENTER));
            table.addCell(styledCell(formatWeight(r.pesoTotal()), cellFont, bg, Element.ALIGN_RIGHT));
            table.addCell(styledCell(r.status() != null ? r.status() : "—", cellFont, bg, Element.ALIGN_LEFT));
        }

        document.add(table);
    }

    private void renderDispatchTable(Document document, List<ReportsDTO.DispatchReportDTO> rows)
            throws DocumentException {
        String[] headers = new String[]{"Despacho", "Pedidos", "Cliente", "Conductor", "Vehículo", "Fecha", "Peso (kg)"};
        float[] widths = new float[]{2, 2.5f, 2.5f, 1.8f, 1.6f, 1.8f, 1.4f};

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setWidths(widths);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(HEADER_BG);
            cell.setPadding(6);
            cell.setHorizontalAlignment("Peso (kg)".equals(h) ? Element.ALIGN_RIGHT : Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
        Font boldCellFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);

        boolean zebra = false;
        for (ReportsDTO.DispatchReportDTO r : rows) {
            Color bg = zebra ? new Color(243, 245, 250) : Color.WHITE;
            zebra = !zebra;

            table.addCell(styledCell(r.dispatchNumber(), boldCellFont, bg, Element.ALIGN_LEFT));
            table.addCell(styledCell(r.orderNumbers() != null ? String.join(", ", r.orderNumbers()) : "—", cellFont, bg, Element.ALIGN_LEFT));
            table.addCell(styledCell(r.customerNames() != null ? String.join(", ", r.customerNames()) : "—", cellFont, bg, Element.ALIGN_LEFT));
            table.addCell(styledCell(r.driverName() != null ? r.driverName() : "—", cellFont, bg, Element.ALIGN_LEFT));
            table.addCell(styledCell(r.vehicleNumber() != null ? r.vehicleNumber() : "—", cellFont, bg, Element.ALIGN_CENTER));
            table.addCell(styledCell(r.dispatchDate() != null ? r.dispatchDate().format(DATE_FMT) : "—", cellFont, bg, Element.ALIGN_CENTER));
            table.addCell(styledCell(formatWeight(r.pesoTotal()), cellFont, bg, Element.ALIGN_RIGHT));
        }

        document.add(table);
    }

    private PdfPCell styledCell(String text, Font font, Color bg, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private String formatWeight(BigDecimal weight) {
        if (weight == null) return "0";
        return weight.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}