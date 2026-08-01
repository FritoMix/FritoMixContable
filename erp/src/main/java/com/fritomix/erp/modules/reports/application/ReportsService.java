package com.fritomix.erp.modules.reports.application;

import com.fritomix.erp.modules.reports.api.ReportsDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportsService {

    private static final Logger log = LoggerFactory.getLogger(ReportsService.class);

    private final EntityManager em;

    @Transactional(readOnly = true)
    public ReportsDTO.KPIResponse getKPIs(String period) {
        LocalDate from = resolveStartDate(period);
        return ReportsDTO.KPIResponse.builder()
                .totalSales(getTotalSales(from))
                .completedOrders(getCompletedOrders(from))
                .dispatchedUnits(getDispatchedUnits(from))
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReportsDTO.TopProduct> getTopProducts(String period) {
        LocalDate from = resolveStartDate(period);
        List<ReportsDTO.TopProduct> result = new ArrayList<>();
        try {
            String sql = """
                    SELECT p.name, p.code, COALESCE(SUM(od.quantity), 0) AS qty,
                           COALESCE(SUM(od.subtotal), 0) AS amount
                    FROM order_details od
                    JOIN orders o ON o.id = od.order_id
                    JOIN products p ON p.id = od.product_id
                    WHERE o.order_date >= :from
                    GROUP BY p.id, p.name, p.code
                    ORDER BY qty DESC
                    LIMIT 10
                    """;
            Query q = em.createNativeQuery(sql);
            q.setParameter("from", from);
            List<Object[]> rows = q.getResultList();
            int rank = 1;
            for (Object[] row : rows) {
                result.add(ReportsDTO.TopProduct.builder()
                        .rank(rank++).name((String) row[0]).code((String) row[1])
                        .units(((Number) row[2]).longValue()).amount((BigDecimal) row[3]).build());
            }
        } catch (Exception e) {
            log.error("Error al obtener top productos desde {}", from, e);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<ReportsDTO.TopClient> getTopClients(String period) {
        LocalDate from = resolveStartDate(period);
        List<ReportsDTO.TopClient> result = new ArrayList<>();
        try {
            String sql = """
                    SELECT COALESCE(c.business_name, '—'), COUNT(o.id), MAX(o.order_date) AS last_date
                    FROM orders o
                    LEFT JOIN customers c ON c.id = o.customer_id
                    WHERE o.order_date >= :from
                    GROUP BY c.id, c.business_name
                    ORDER BY last_date DESC
                    LIMIT 10
                    """;
            Query q = em.createNativeQuery(sql);
            q.setParameter("from", from);
            List<Object[]> rows = q.getResultList();
            for (Object[] row : rows) {
                Object dateObj = row[2];
                LocalDate lastDate = null;
                if (dateObj instanceof java.sql.Date d) {
                    lastDate = d.toLocalDate();
                } else if (dateObj instanceof java.sql.Timestamp ts) {
                    lastDate = ts.toLocalDateTime().toLocalDate();
                } else if (dateObj instanceof LocalDate ld) {
                    lastDate = ld;
                }
                result.add(ReportsDTO.TopClient.builder()
                        .name((String) row[0]).orders(((Number) row[1]).longValue())
                        .lastOrderDate(lastDate).build());
            }
        } catch (Exception e) {
            log.error("Error al obtener top clientes desde {}", from, e);
        }
        return result;
    }

    private BigDecimal getTotalSales(LocalDate from) {
        try {
            Query q = em.createNativeQuery("SELECT COALESCE(SUM(total), 0) FROM orders WHERE order_date >= :from AND status = 'ENTREGADO'");
            q.setParameter("from", from);
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            log.error("Error al obtener total de ventas desde {}", from, e);
            return BigDecimal.ZERO;
        }
    }

    private long getCompletedOrders(LocalDate from) {
        try {
            Query q = em.createNativeQuery("SELECT COUNT(*) FROM orders WHERE order_date >= :from AND status = 'ENTREGADO'");
            q.setParameter("from", from);
            return ((Number) q.getSingleResult()).longValue();
        } catch (Exception e) {
            log.error("Error al obtener pedidos completados desde {}", from, e);
            return 0;
        }
    }

    private long getDispatchedUnits(LocalDate from) {
        try {
            Query q = em.createNativeQuery("SELECT COALESCE(SUM(od.quantity), 0) FROM order_details od JOIN orders o ON o.id = od.order_id WHERE o.order_date >= :from");
            q.setParameter("from", from);
            return ((Number) q.getSingleResult()).longValue();
        } catch (Exception e) {
            log.error("Error al obtener unidades despachadas desde {}", from, e);
            return 0;
        }
    }

    private LocalDate resolveStartDate(String period) {
        return switch (period == null ? "mes" : period) {
            case "hoy" -> LocalDate.now();
            case "semana" -> LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case "mes" -> LocalDate.now().withDayOfMonth(1);
            case "trimestre" -> {
                int quarterStartMonth = ((LocalDate.now().getMonthValue() - 1) / 3) * 3 + 1;
                yield LocalDate.now().withMonth(quarterStartMonth).withDayOfMonth(1);
            }
            case "ano" -> LocalDate.now().withDayOfYear(1);
            default -> LocalDate.now().withDayOfMonth(1);
        };
    }
}
