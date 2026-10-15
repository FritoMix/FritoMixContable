package com.fritomix.erp.modules.camaras.application.service;

import com.fritomix.erp.modules.camaras.application.dto.response.CamaraVerificacionResponse;
import com.fritomix.erp.modules.camaras.application.dto.response.CamaraVerificacionResponse.DetalleVerificacionResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class CamaraWordService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarActaVerificacionWord(CamaraVerificacionResponse v) {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Document header & style settings
            XWPFParagraph titlePara = document.createParagraph();
            titlePara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText("FRITOMIX S.A.S.");
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setFontFamily("Arial");
            titleRun.setColor("1E3A8A");

            XWPFParagraph subTitlePara = document.createParagraph();
            subTitlePara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun subTitleRun = subTitlePara.createRun();
            subTitleRun.setText("ACTA OFICIAL DE VERIFICACIÓN Y CONTROL DE CÁMARAS");
            subTitleRun.setBold(true);
            subTitleRun.setFontSize(14);
            subTitleRun.setFontFamily("Arial");
            subTitleRun.setColor("374151");

            addEmptyLine(document);

            // General Information Table
            XWPFTable infoTable = document.createTable(6, 2);
            infoTable.setWidth("100%");

            setCellHeader(infoTable, 0, 0, "No. Despacho:");
            setCellValue(infoTable, 0, 1, v.dispatchNumber() != null ? v.dispatchNumber() : "N/A");

            setCellHeader(infoTable, 1, 0, "Cliente:");
            setCellValue(infoTable, 1, 1, v.clienteNombre() != null ? v.clienteNombre() : "N/A");

            setCellHeader(infoTable, 2, 0, "Fecha Despacho:");
            setCellValue(infoTable, 2, 1, v.fechaDespacho() != null ? v.fechaDespacho().format(DATE_FORMATTER) : "N/A");

            setCellHeader(infoTable, 3, 0, "Vehículo / Conductor:");
            String vehCond = String.format("%s / %s",
                    v.vehiclePlate() != null ? v.vehiclePlate() : "N/A",
                    v.driverName() != null ? v.driverName() : "N/A");
            setCellValue(infoTable, 3, 1, vehCond);

            setCellHeader(infoTable, 4, 0, "Estado Cámaras:");
            setCellValue(infoTable, 4, 1, v.estadoCamaras());

            setCellHeader(infoTable, 5, 0, "Verificado Por / Fecha:");
            String verif = String.format("%s (%s)",
                    v.verificadoPorNombre() != null ? v.verificadoPorNombre() : "N/A",
                    v.fechaInicioVerificacion() != null ? v.fechaInicioVerificacion().format(DATE_FORMATTER) : "N/A");
            setCellValue(infoTable, 5, 1, verif);

            addEmptyLine(document);

            // Detail Table Header
            XWPFParagraph sectionHeader = document.createParagraph();
            XWPFRun sectionRun = sectionHeader.createRun();
            sectionRun.setText("DETALLE DE VERIFICACIÓN DE PRODUCTOS Y CANTIDADES");
            sectionRun.setBold(true);
            sectionRun.setFontSize(12);
            sectionRun.setFontFamily("Arial");
            sectionRun.setColor("1E3A8A");

            List<DetalleVerificacionResponse> detalles = v.detalles();
            int rows = (detalles != null ? detalles.size() : 0) + 1;
            XWPFTable detailTable = document.createTable(rows, 6);
            detailTable.setWidth("100%");

            // Table Header Row
            String[] headers = {"Código", "Producto", "Despachado", "Verificado", "Diferencia", "Estado"};
            XWPFTableRow headerRow = detailTable.getRow(0);
            for (int i = 0; i < headers.length; i++) {
                XWPFTableCell cell = headerRow.getCell(i);
                cell.setColor("1E3A8A");
                XWPFParagraph p = cell.getParagraphs().get(0);
                p.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun r = p.createRun();
                r.setText(headers[i]);
                r.setBold(true);
                r.setColor("FFFFFF");
                r.setFontSize(10);
                r.setFontFamily("Arial");
            }

            // Populate rows
            if (detalles != null) {
                for (int i = 0; i < detalles.size(); i++) {
                    DetalleVerificacionResponse det = detalles.get(i);
                    XWPFTableRow row = detailTable.getRow(i + 1);

                    setCellText(row.getCell(0), det.productCode() != null ? det.productCode() : "-", false, ParagraphAlignment.LEFT);
                    setCellText(row.getCell(1), det.productName() != null ? det.productName() : "-", false, ParagraphAlignment.LEFT);
                    setCellText(row.getCell(2), det.cantidadDespachada() != null ? det.cantidadDespachada().toString() : "0", false, ParagraphAlignment.RIGHT);
                    setCellText(row.getCell(3), det.cantidadVerificada() != null ? det.cantidadVerificada().toString() : "0", false, ParagraphAlignment.RIGHT);
                    setCellText(row.getCell(4), det.diferencia() != null ? det.diferencia().toString() : "0", false, ParagraphAlignment.RIGHT);

                    String estado = det.estadoLinea();
                    XWPFTableCell estadoCell = row.getCell(5);
                    if ("DIFERENCIA".equalsIgnoreCase(estado)) {
                        estadoCell.setColor("FEE2E2"); // soft red
                        setCellText(estadoCell, "DIFERENCIA", true, ParagraphAlignment.CENTER);
                    } else {
                        estadoCell.setColor("DCFCE7"); // soft green
                        setCellText(estadoCell, "OK", true, ParagraphAlignment.CENTER);
                    }
                }
            }

            addEmptyLine(document);

            // Observaciones generales
            XWPFParagraph obsTitle = document.createParagraph();
            XWPFRun obsTitleRun = obsTitle.createRun();
            obsTitleRun.setText("OBSERVACIONES GENERALES:");
            obsTitleRun.setBold(true);
            obsTitleRun.setFontSize(11);
            obsTitleRun.setFontFamily("Arial");

            XWPFParagraph obsPara = document.createParagraph();
            XWPFRun obsRun = obsPara.createRun();
            obsRun.setText(v.observacionesGenerales() != null && !v.observacionesGenerales().isBlank()
                    ? v.observacionesGenerales()
                    : "Sin observaciones registradas.");
            obsRun.setItalic(true);
            obsRun.setFontSize(10);
            obsRun.setFontFamily("Arial");

            addEmptyLine(document);
            addEmptyLine(document);

            // Signatures Table
            XWPFTable signTable = document.createTable(1, 2);
            signTable.setWidth("100%");

            XWPFTableCell cell1 = signTable.getRow(0).getCell(0);
            setCellText(cell1, "_____________________________________\nFirma Personal de Cámaras\nNombre: " + (v.verificadoPorNombre() != null ? v.verificadoPorNombre() : ""), false, ParagraphAlignment.CENTER);

            XWPFTableCell cell2 = signTable.getRow(0).getCell(1);
            setCellText(cell2, "_____________________________________\nFirma Aprobador / Supervisor\nNombre: " + (v.aprobadoPorNombre() != null ? v.aprobadoPorNombre() : ""), false, ParagraphAlignment.CENTER);

            document.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error al generar el acta de verificación en Word para despacho id={}", v.dispatchId(), e);
            throw new RuntimeException("Error interno al generar documento Word de Cámaras", e);
        }
    }

    private void addEmptyLine(XWPFDocument doc) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setText("");
    }

    private void setCellHeader(XWPFTable table, int row, int col, String text) {
        XWPFTableCell cell = table.getRow(row).getCell(col);
        cell.setColor("F3F4F6");
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun r = p.createRun();
        r.setText(text);
        r.setBold(true);
        r.setFontSize(10);
        r.setFontFamily("Arial");
    }

    private void setCellValue(XWPFTable table, int row, int col, String text) {
        XWPFTableCell cell = table.getRow(row).getCell(col);
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun r = p.createRun();
        r.setText(text != null ? text : "");
        r.setFontSize(10);
        r.setFontFamily("Arial");
    }

    private void setCellText(XWPFTableCell cell, String text, boolean bold, ParagraphAlignment alignment) {
        XWPFParagraph p = cell.getParagraphs().isEmpty() ? cell.addParagraph() : cell.getParagraphs().get(0);
        p.setAlignment(alignment);
        XWPFRun r = p.createRun();
        r.setText(text);
        r.setBold(bold);
        r.setFontSize(9);
        r.setFontFamily("Arial");
    }
}
