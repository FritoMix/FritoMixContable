package com.fritomix.erp.modules.dashboard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EntityManager em;

    @Transactional(readOnly = true)
    public DashboardDTO getDashboard() {
        long ordersToday = countOrdersToday();
        long pendingDispatches = countPendingDispatches();
        long totalProducts = countTotal("products");
        long totalCustomers = countTotal("customers");
        List<DashboardDTO.MonthlySales> monthlySales = getMonthlySales();
        List<DashboardDTO.OrdersByStatus> ordersByStatus = getOrdersByStatus();
        List<DashboardDTO.TopProduct> topProducts = getTopProducts();
        List<DashboardDTO.RecentOrder> recentOrders = getRecentOrders();

        return DashboardDTO.builder()
                .ordersToday(ordersToday)
                .pendingDispatches(pendingDispatches)
                .totalProducts(totalProducts)
                .totalCustomers(totalCustomers)
                .monthlySales(monthlySales)
                .ordersByStatus(ordersByStatus)
                .topProducts(topProducts)
                .recentOrders(recentOrders)
                .build();
    }

    private long countOrdersToday() {
        try {
            Query q = em.createNativeQuery("SELECT COUNT(*) FROM orders WHERE DATE(order_date) = :today");
            q.setParameter("today", LocalDate.now());
            return ((Number) q.getSingleResult()).longValue();
        } catch (Exception e) {
            return 0;
        }
    }

    private long countPendingDispatches() {
        try {
            Query q = em.createNativeQuery("SELECT COUNT(*) FROM dispatches WHERE status = 'PENDIENTE'");
            return ((Number) q.getSingleResult()).longValue();
        } catch (Exception e) {
            return 0;
        }
    }

    private long countTotal(String table) {
        try {
            Query q = em.createNativeQuery("SELECT COUNT(*) FROM " + table);
            return ((Number) q.getSingleResult()).longValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private List<DashboardDTO.MonthlySales> getMonthlySales() {
        List<DashboardDTO.MonthlySales> result = new ArrayList<>();
        try {
            String sql = """
                    SELECT COALESCE(EXTRACT(MONTH FROM o.order_date), 0) AS mes,
                           COUNT(o.id) AS cnt,
                           COALESCE(SUM(o.total), 0) AS tot
                    FROM orders o
                    WHERE EXTRACT(YEAR FROM o.order_date) = EXTRACT(YEAR FROM CURRENT_DATE)
                    GROUP BY EXTRACT(MONTH FROM o.order_date)
                    ORDER BY mes
                    """;
            Query q = em.createNativeQuery(sql);
            List<Object[]> rows = q.getResultList();
            for (Object[] row : rows) {
                int month = ((Number) row[0]).intValue();
                long count = ((Number) row[1]).longValue();
                BigDecimal total = (BigDecimal) row[2];
                String monthName = switch (month) {
                    case 1 -> "Ene"; case 2 -> "Feb"; case 3 -> "Mar"; case 4 -> "Abr";
                    case 5 -> "May"; case 6 -> "Jun"; case 7 -> "Jul"; case 8 -> "Ago";
                    case 9 -> "Sep"; case 10 -> "Oct"; case 11 -> "Nov"; case 12 -> "Dic";
                    default -> "?";
                };
                result.add(DashboardDTO.MonthlySales.builder()
                        .month(monthName).count(count).total(total).build());
            }
        } catch (Exception ignored) {}
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<DashboardDTO.OrdersByStatus> getOrdersByStatus() {
        List<DashboardDTO.OrdersByStatus> result = new ArrayList<>();
        try {
            Query q = em.createNativeQuery(
                    "SELECT COALESCE(status, 'PENDING'), COUNT(*) FROM orders GROUP BY status");
            List<Object[]> rows = q.getResultList();
            for (Object[] row : rows) {
                result.add(DashboardDTO.OrdersByStatus.builder()
                        .status((String) row[0])
                        .count(((Number) row[1]).longValue())
                        .build());
            }
        } catch (Exception ignored) {}
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<DashboardDTO.TopProduct> getTopProducts() {
        List<DashboardDTO.TopProduct> result = new ArrayList<>();
        try {
            String sql = """
                    SELECT p.name, p.code, COALESCE(SUM(od.quantity), 0) AS total_qty
                    FROM order_details od
                    JOIN products p ON p.id = od.product_id
                    GROUP BY p.id, p.name, p.code
                    ORDER BY total_qty DESC
                    LIMIT 5
                    """;
            Query q = em.createNativeQuery(sql);
            List<Object[]> rows = q.getResultList();
            for (Object[] row : rows) {
                result.add(DashboardDTO.TopProduct.builder()
                        .name((String) row[0])
                        .code((String) row[1])
                        .units((BigDecimal) row[2])
                        .build());
            }
        } catch (Exception ignored) {}
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<DashboardDTO.RecentOrder> getRecentOrders() {
        List<DashboardDTO.RecentOrder> result = new ArrayList<>();
        try {
            String sql = """
                    SELECT o.order_number,
                           COALESCE(c.business_name, '—'),
                           o.status,
                           TO_CHAR(o.order_date, 'DD/MM/YYYY')
                    FROM orders o
                    LEFT JOIN customers c ON c.id = o.customer_id
                    ORDER BY o.created_at DESC
                    LIMIT 5
                    """;
            Query q = em.createNativeQuery(sql);
            List<Object[]> rows = q.getResultList();
            for (Object[] row : rows) {
                result.add(DashboardDTO.RecentOrder.builder()
                        .id((String) row[0])
                        .client((String) row[1])
                        .status((String) row[2])
                        .date((String) row[3])
                        .build());
            }
        } catch (Exception ignored) {}
        return result;
    }
}
