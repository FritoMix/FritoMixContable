package com.fritomix.erp.modules.orders.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderStatusRulesTest {

    @Test
    void isValidStatus_shouldAcceptKnownStates() {
        assertTrue(OrderStatusRules.isValidStatus("PENDIENTE"));
        assertTrue(OrderStatusRules.isValidStatus("APROBADO"));
        assertTrue(OrderStatusRules.isValidStatus("CANCELADO"));
        assertTrue(OrderStatusRules.isValidStatus("EN_PRODUCCION"));
        assertTrue(OrderStatusRules.isValidStatus("LISTO_PRODUCCION"));
        assertFalse(OrderStatusRules.isValidStatus("ENVIADO"));
        assertFalse(OrderStatusRules.isValidStatus(null));
    }

    @Test
    void canTransition_shouldFollowFlow() {
        assertTrue(OrderStatusRules.canTransition("PENDIENTE", "PENDIENTE"));
        assertTrue(OrderStatusRules.canTransition("PENDIENTE", "APROBADO"));
        assertTrue(OrderStatusRules.canTransition("PENDIENTE", "CANCELADO"));
        assertTrue(OrderStatusRules.canTransition("APROBADO", "CANCELADO"));
        assertTrue(OrderStatusRules.canTransition("APROBADO", "EN_PRODUCCION"));
        assertTrue(OrderStatusRules.canTransition("EN_PRODUCCION", "LISTO_PRODUCCION"));
        assertTrue(OrderStatusRules.canTransition("EN_PRODUCCION", "CANCELADO"));
        assertFalse(OrderStatusRules.canTransition("APROBADO", "PENDIENTE"));
        assertFalse(OrderStatusRules.canTransition("APROBADO", "LISTO_PRODUCCION"));
        assertFalse(OrderStatusRules.canTransition("CANCELADO", "APROBADO"));
        assertFalse(OrderStatusRules.canTransition("CANCELADO", "PENDIENTE"));
        assertFalse(OrderStatusRules.canTransition("LISTO_PRODUCCION", "EN_PRODUCCION"));
    }

    @Test
    void isClosed_shouldBeTrueForTerminalStates() {
        assertTrue(OrderStatusRules.isClosed("APROBADO"));
        assertTrue(OrderStatusRules.isClosed("CANCELADO"));
        assertTrue(OrderStatusRules.isClosed("LISTO_PRODUCCION"));
        assertFalse(OrderStatusRules.isClosed("PENDIENTE"));
        assertFalse(OrderStatusRules.isClosed("EN_PRODUCCION"));
    }

    @Test
    void isProductionStatus_shouldAcceptOnlyProductionStates() {
        assertTrue(OrderStatusRules.isProductionStatus("EN_PRODUCCION"));
        assertTrue(OrderStatusRules.isProductionStatus("LISTO_PRODUCCION"));
        assertFalse(OrderStatusRules.isProductionStatus("APROBADO"));
        assertFalse(OrderStatusRules.isProductionStatus("PENDIENTE"));
        assertFalse(OrderStatusRules.isProductionStatus(null));
    }
}