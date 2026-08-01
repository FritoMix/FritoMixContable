package com.fritomix.erp.modules.reports.api;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class ReportsDTO {

    private ReportsDTO() {}

    @Builder
    public record KPIResponse(BigDecimal totalSales, long completedOrders, long dispatchedUnits) {}

    @Builder
    public record TopProduct(int rank, String name, String code, long units, BigDecimal amount) {}

    @Builder
    public record TopClient(String name, long orders, LocalDate lastOrderDate) {}

    @Builder
    public record ReportFilters(String period, String type) {}
}
