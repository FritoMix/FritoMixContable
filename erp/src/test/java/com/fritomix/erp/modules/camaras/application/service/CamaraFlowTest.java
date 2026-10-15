package com.fritomix.erp.modules.camaras.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CamaraFlowTest {

    @Test
    @DisplayName("Transición válida de PENDIENTE_CAMARAS a EN_VERIFICACION")
    void testTransicionIniciado() {
        assertDoesNotThrow(() ->
                CamaraFlow.validateTransition(CamaraFlow.PENDIENTE_CAMARAS, CamaraFlow.EN_VERIFICACION)
        );
    }

    @Test
    @DisplayName("Transición válida de EN_VERIFICACION a APROBADA")
    void testTransicionAprobado() {
        assertDoesNotThrow(() ->
                CamaraFlow.validateTransition(CamaraFlow.EN_VERIFICACION, CamaraFlow.APROBADA)
        );
    }

    @Test
    @DisplayName("Transición inválida de PENDIENTE_CAMARAS a APROBADA debe lanzar IllegalArgumentException")
    void testTransicionInvalidaDirecta() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                CamaraFlow.validateTransition(CamaraFlow.PENDIENTE_CAMARAS, CamaraFlow.APROBADA)
        );
        assertTrue(ex.getMessage().contains("Transición no permitida"));
    }

    @Test
    @DisplayName("Ficha APROBADA no se puede modificar")
    void testModificableAprobada() {
        assertTrue(CamaraFlow.isEstadoCerrado(CamaraFlow.APROBADA));
    }

    @Test
    @DisplayName("Ficha EN_VERIFICACION no es cerrada")
    void testModificableEnVerificacion() {
        assertFalse(CamaraFlow.isEstadoCerrado(CamaraFlow.EN_VERIFICACION));
    }
}
