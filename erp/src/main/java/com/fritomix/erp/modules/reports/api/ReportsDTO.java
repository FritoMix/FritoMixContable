package com.fritomix.erp.modules.reports.api;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class ReportsDTO {

    private ReportsDTO() {}

    @Builder
    public record OrderReportDTO(
            Long id,
            String orderNumber,
            String customerName,
            String city,
            String department,
            String address,
            String phone,
            LocalDateTime orderDate,
            String status,
            BigDecimal pesoTotal
    ) {}

    @Builder
    public record DispatchReportDTO(
            Long id,
            String dispatchNumber,
            List<String> orderNumbers,
            List<String> customerNames,
            String city,
            String address,
            LocalDateTime dispatchDate,
            String driverName,
            String vehicleNumber,
            String status,
            BigDecimal pesoTotal
    ) {}
}
