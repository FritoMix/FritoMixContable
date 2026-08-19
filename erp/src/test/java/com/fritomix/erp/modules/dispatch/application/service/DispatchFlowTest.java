package com.fritomix.erp.modules.dispatch.application.service;

import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.products.domain.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DispatchFlowTest {

    @Test
    void initialStatus_shouldDefaultToPendiente() {
        assertEquals("PENDIENTE", DispatchFlow.initialStatus(null));
        assertEquals("ELABORACION", DispatchFlow.initialStatus("elaboracion"));
    }

    @Test
    void initialStatus_shouldRejectAdvancedStates() {
        assertThrows(IllegalArgumentException.class, () -> DispatchFlow.initialStatus("PRODUCCION"));
        assertThrows(IllegalArgumentException.class, () -> DispatchFlow.initialStatus("DESPACHADO"));
    }

    @Test
    void validateTransition_shouldAllowForwardAndSame() {
        DispatchFlow.validateTransition("PENDIENTE", "PENDIENTE");
        DispatchFlow.validateTransition("ELABORACION", "PRODUCCION");
        DispatchFlow.validateTransition("LISTO_CARGUE", "DESPACHADO");
    }

    @Test
    void validateTransition_shouldRejectBackwardOrInvalid() {
        assertThrows(IllegalArgumentException.class, () -> DispatchFlow.validateTransition("PRODUCCION", "PENDIENTE"));
        assertThrows(IllegalArgumentException.class, () -> DispatchFlow.validateTransition("PENDIENTE", "INVALIDO"));
    }

    @Test
    void isClosed_shouldOnlyBeDespachado() {
        assertTrue(DispatchFlow.isClosed("DESPACHADO"));
        assertFalse(DispatchFlow.isClosed("LISTO_CARGUE"));
    }

    @Test
    void calcularCumplimiento_shouldBeCompleteWhenAllDelivered() {
        DispatchDetail full = detail(new BigDecimal("10"), new BigDecimal("10"));
        DispatchDetail partial = detail(new BigDecimal("5"), new BigDecimal("5"));
        assertEquals("COMPLETO", DispatchFlow.calcularCumplimiento(List.of(full, partial)));
    }

    @Test
    void calcularCumplimiento_shouldBePartialWhenUnderDelivered() {
        DispatchDetail partial = detail(new BigDecimal("10"), new BigDecimal("7"));
        assertEquals("PARCIAL", DispatchFlow.calcularCumplimiento(List.of(partial)));
        assertEquals("PARCIAL", DispatchFlow.calcularCumplimiento(List.of()));
    }

    private DispatchDetail detail(BigDecimal quantity, BigDecimal delivered) {
        return DispatchDetail.builder()
                .product(Product.builder().id(1L).build())
                .quantity(quantity)
                .delivered(delivered)
                .build();
    }
}