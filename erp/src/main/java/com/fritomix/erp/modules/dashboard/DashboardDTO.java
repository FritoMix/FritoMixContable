package com.fritomix.erp.modules.dashboard;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record DashboardDTO(
        long ordersToday,
        long pendingDispatches,
        long totalProducts,
        long totalCustomers,
        List<MonthlySales> monthlySales,
        List<OrdersByStatus> ordersByStatus,
        List<TopProduct> topProducts,
        List<RecentOrder> recentOrders
) {
    @Builder
    public record MonthlySales(String month, long count, BigDecimal total) {}

    @Builder
    public record OrdersByStatus(String status, long count) {}

    @Builder
    public record TopProduct(String name, String code, BigDecimal units) {}

    @Builder
    public record RecentOrder(String id, String client, String status, String date) {}
}
