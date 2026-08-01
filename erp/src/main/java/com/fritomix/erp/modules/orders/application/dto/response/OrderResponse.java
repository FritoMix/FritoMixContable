package com.fritomix.erp.modules.orders.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(
        Long id,
        String orderNumber,
        Long customerId,
        String customerName,
        String customerDocument,
        String phone,
        String address,
        String cityName,
        String departmentName,
        Long userId,
        String coordinatorName,
        LocalDateTime orderDate,
        String status,
        BigDecimal total,
        BigDecimal pesoTotalCargue,
        String notes,
        String dispatchUserName,
        String dispatchDriverName,
        String dispatchDriverDocument,
        String dispatchDriverPhone,
        String dispatchVehiclePlate,
        LocalDateTime dispatchDate,
        List<OrderDetailResponse> details,
        LocalDateTime createdAt
) {

    @Builder
    public record OrderDetailResponse(
            Long id,
            Long productId,
            String productName,
            String productCode,
            String productType,
            BigDecimal pesoUnidad,
            BigDecimal dimension,
            BigDecimal quantity
    ) {}
}
