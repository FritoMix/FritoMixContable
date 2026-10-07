package com.fritomix.erp.modules.reports.api;

import com.fritomix.erp.modules.auth.application.dto.JwtUserInfo;
import com.fritomix.erp.modules.notifications.application.service.NotificationService;
import com.fritomix.erp.modules.reports.application.ReportsService;
import com.fritomix.erp.modules.reports.application.dto.ReportsDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportsControllerTest {

    @Mock
    private ReportsService reportsService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReportsController controller;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private ReportsDTO.OrderReportDTO orderReport() {
        return ReportsDTO.OrderReportDTO.builder()
                .id(1L).orderNumber("ORD-1").customerName("Cliente")
                .city("Medellín").department("Antioquia").address("Calle 1").phone("300")
                .orderDate(LocalDateTime.of(2026, 1, 2, 10, 0)).status("APROBADO")
                .pesoTotal(new BigDecimal("12.5")).build();
    }

    private ReportsDTO.DispatchReportDTO dispatchReport() {
        return ReportsDTO.DispatchReportDTO.builder()
                .id(1L).dispatchNumber("D-1").orderNumbers(List.of("ORD-1", "ORD-2"))
                .customerNames(List.of("Cliente")).city("Bogotá")
                .dispatchDate(LocalDateTime.of(2026, 1, 3, 8, 0))
                .driverName("Pedro").vehicleNumber("ABC123").status("DESPACHADO")
                .pesoTotal(new BigDecimal("30")).build();
    }

    @Test
    void getOrdersConEstadoInvalidoDevuelveBadRequest() {
        ResponseEntity<?> response = controller.getOrders("NO_EXISTE");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Estado inválido"));
    }

    @Test
    void getOrdersValidoDelegaEnServicio() {
        when(reportsService.getOrdersByStatus("APROBADO")).thenReturn(List.of(orderReport()));

        ResponseEntity<?> response = controller.getOrders("APROBADO");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void getLogisticaDelegaEnServicio() {
        when(reportsService.getDespachados()).thenReturn(List.of(dispatchReport()));

        ResponseEntity<List<ReportsDTO.DispatchReportDTO>> response = controller.getLogistica();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void downloadPdfTipoInvalidoDevuelveBadRequest() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.downloadPdf("no-valido", response);

        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("Tipo inválido"));
    }

    @Test
    void downloadPdfDePedidosGeneraPdf() throws Exception {
        when(reportsService.getOrdersByStatus("APROBADO")).thenReturn(List.of(orderReport()));
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.downloadPdf("aprobados", response);

        assertEquals(200, response.getStatus());
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.getContentAsByteArray().length > 0);
        assertTrue(response.getHeader("Content-Disposition").contains("reporte-aprobados.pdf"));
    }

    @Test
    void downloadPdfDeLogisticaGeneraPdfYNotifica() throws Exception {
        when(reportsService.getDespachados()).thenReturn(List.of(dispatchReport()));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new JwtUserInfo(7L, "u@x.com", "ADMIN", "Ana", "Soto", List.of()), null, List.of()));
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.downloadPdf("logistica", response);

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsByteArray().length > 0);
        verify(notificationService).create(any());
    }

    @Test
    void downloadPdfConListaVaciaNoFalla() throws Exception {
        when(reportsService.getOrdersByStatus("PENDIENTE")).thenReturn(List.of());
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.downloadPdf("pendientes", response);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getContentAsByteArray());
    }
}