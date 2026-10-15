package com.fritomix.erp.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void conflictoOptimistaDevuelve409ConMensajeDeRecarga() {
        ResponseEntity<Map<String, Object>> resp = handler.handleOptimisticLock(
                new ObjectOptimisticLockingFailureException("stale row", new RuntimeException("boom")));

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals(409, resp.getBody().get("status"));
        assertTrue(resp.getBody().get("error").toString().contains("otro usuario"));
    }

    @Test
    void conflictoDeIntegridadDevuelve409() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleDataIntegrityViolation(new DataIntegrityViolationException("dup"));

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals(409, resp.getBody().get("status"));
    }

    @Test
    void argumentoInvalidoDevuelve400DelMensaje() {
        ResponseEntity<Map<String, Object>> resp =
                handler.handleIllegalArgument(new IllegalArgumentException("tipo_pedido inválido"));

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("tipo_pedido inválido", resp.getBody().get("error"));
    }
}