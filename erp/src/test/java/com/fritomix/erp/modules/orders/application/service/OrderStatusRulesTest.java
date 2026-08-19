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
        assertFalse(OrderStatusRules.isValidStatus("ENVIADO"));
        assertFalse(OrderStatusRules.isValidStatus(null));
    }

    @Test
    void canTransition_shouldFollowFlow() {
        assertTrue(OrderStatusRules.canTransition("PENDIENTE", "PENDIENTE"));
        assertTrue(OrderStatusRules.canTransition("PENDIENTE", "APROBADO"));
        assertTrue(OrderStatusRules.canTransition("PENDIENTE", "CANCELADO"));
        assertTrue(OrderStatusRules.canTransition("APROBADO", "CANCELADO"));
        assertFalse(OrderStatusRules.canTransition("APROBADO", "PENDIENTE"));
        assertFalse(OrderStatusRules.canTransition("CANCELADO", "APROBADO"));
        assertFalse(OrderStatusRules.canTransition("CANCELADO", "PENDIENTE"));
    }

    @Test
    void isClosed_shouldBeTrueForAprobadoAndCancelado() {
        assertTrue(OrderStatusRules.isClosed("APROBADO"));
        assertTrue(OrderStatusRules.isClosed("CANCELADO"));
        assertFalse(OrderStatusRules.isClosed("PENDIENTE"));
    }
}