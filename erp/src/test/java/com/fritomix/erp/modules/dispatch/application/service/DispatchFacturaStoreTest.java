package com.fritomix.erp.modules.dispatch.application.service;

import com.fritomix.erp.modules.dispatch.application.dto.request.DispatchRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatchFacturaStoreTest {

    @Mock private EntityManager em;
    @InjectMocks private DispatchFacturaStore store;

    @Test
    void saveFacturasConListaNulaOVaciaNoTocaLaBase() {
        store.saveFacturas(1L, null);
        store.saveFacturas(1L, List.of());

        verify(em, never()).createNativeQuery(anyString());
    }

    @Test
    void saveFacturasActualizaElNumeroDeFactura() {
        Query q = mock(Query.class);
        when(em.createNativeQuery(anyString())).thenReturn(q);
        when(q.setParameter(anyString(), any())).thenReturn(q);
        when(q.executeUpdate()).thenReturn(1);

        store.saveFacturas(7L, List.of(
                DispatchRequest.OrderFacturaRequest.builder()
                        .orderId(3L).numeroFactura("F-100").build()));

        verify(em).createNativeQuery("UPDATE dispatch_orders SET numero_factura = :numeroFactura WHERE dispatch_id = :dispatchId AND order_id = :orderId");
        verify(q).setParameter("numeroFactura", "F-100");
        verify(q).setParameter("dispatchId", 7L);
        verify(q).setParameter("orderId", 3L);
        verify(q).executeUpdate();
    }

    @Test
    void saveFacturasIgnoraFilasSinOrderId() {
        store.saveFacturas(7L, List.of(
                DispatchRequest.OrderFacturaRequest.builder()
                        .orderId(null).numeroFactura("F-100").build()));

        verify(em, never()).createNativeQuery(anyString());
    }

    @Test
    void loadFacturasConIdNuloDevuelveMapaVacio() {
        assertTrue(store.loadFacturas(null).isEmpty());
    }

    @Test
    void loadFacturasMapeaFilasDeLaTablaDeEnlace() {
        Query q = mock(Query.class);
        when(em.createNativeQuery(anyString())).thenReturn(q);
        when(q.setParameter(anyString(), any())).thenReturn(q);
        when(q.getResultList()).thenReturn(Arrays.asList((Object) new Object[]{3L, "F-100"}));

        Map<Long, String> facturas = store.loadFacturas(7L);

        assertEquals(Map.of(3L, "F-100"), facturas);
    }

    @Test
    void loadFacturasConResultadoVacioDevuelveMapaVacio() {
        Query q = mock(Query.class);
        when(em.createNativeQuery(anyString())).thenReturn(q);
        when(q.setParameter(anyString(), any())).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of());

        assertTrue(store.loadFacturas(7L).isEmpty());
    }
}