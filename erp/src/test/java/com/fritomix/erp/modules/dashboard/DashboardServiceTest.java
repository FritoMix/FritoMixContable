package com.fritomix.erp.modules.dashboard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private DashboardService service;

    private Query countQuery(long value) {
        Query q = mock(Query.class);
        when(q.getSingleResult()).thenReturn(value);
        return q;
    }

    @SuppressWarnings("unchecked")
    private Query listQuery(Object[][] rows) {
        Query q = mock(Query.class);
        when(q.getResultList()).thenReturn(List.of(rows));
        return q;
    }

    private void stubQueries() {
        when(em.createNativeQuery(anyString())).thenAnswer(inv -> {
            String sql = inv.getArgument(0);
            if (sql.contains("DATE(order_date)")) return countQuery(1);
            if (sql.contains("FROM dispatches")) return countQuery(2);
            if (sql.contains("FROM products")) return countQuery(3);
            if (sql.contains("FROM customers")) return countQuery(4);
            if (sql.contains("EXTRACT(MONTH")) return listQuery(new Object[][]{
                    {1, 5L, new BigDecimal("100.50")},
                    {13, 1L, null}});
            if (sql.contains("GROUP BY status")) return listQuery(new Object[][]{{"pendiente", 3L}});
            if (sql.contains("JOIN products p")) return listQuery(new Object[][]{{"Prod", "P1", new BigDecimal("42")}});
            if (sql.contains("TO_CHAR")) return listQuery(new Object[][]{{"ORD-1", "Cliente", "pendiente", "2026-01-01"}});
            return mock(Query.class);
        });
    }

    @Test
    void getDashboardAgregaContadoresYListas() {
        stubQueries();

        DashboardDTO dto = service.getDashboard();

        assertEquals(1, dto.ordersToday());
        assertEquals(2, dto.pendingDispatches());
        assertEquals(3, dto.totalProducts());
        assertEquals(4, dto.totalCustomers());

        assertEquals(2, dto.monthlySales().size());
        assertEquals("Ene", dto.monthlySales().get(0).month());
        assertEquals(5, dto.monthlySales().get(0).count());
        assertEquals(0, new BigDecimal("100.50").compareTo(dto.monthlySales().get(0).total()));
        assertEquals("?", dto.monthlySales().get(1).month());

        assertEquals(1, dto.ordersByStatus().size());
        assertEquals("pendiente", dto.ordersByStatus().get(0).status());
        assertEquals(3, dto.ordersByStatus().get(0).count());

        assertEquals(1, dto.topProducts().size());
        assertEquals("Prod", dto.topProducts().get(0).name());

        assertEquals(1, dto.recentOrders().size());
        assertEquals("ORD-1", dto.recentOrders().get(0).id());
        assertEquals("Cliente", dto.recentOrders().get(0).client());
    }

    @Test
    void getDashboardDevuelveValoresPorDefectoSiFallaLaConsulta() {
        when(em.createNativeQuery(anyString())).thenThrow(new RuntimeException("db caída"));

        DashboardDTO dto = service.getDashboard();

        assertEquals(0, dto.ordersToday());
        assertEquals(0, dto.pendingDispatches());
        assertEquals(0, dto.totalProducts());
        assertEquals(0, dto.totalCustomers());
        assertTrue(dto.monthlySales().isEmpty());
        assertTrue(dto.ordersByStatus().isEmpty());
        assertTrue(dto.topProducts().isEmpty());
        assertTrue(dto.recentOrders().isEmpty());
    }
}