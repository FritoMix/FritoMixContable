package com.fritomix.erp.modules.dispatch.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DispatchResponse(
        Long id,
        String dispatchNumber,
        Long orderId,
        String orderNumber,
        BigDecimal pesoTotalCargue,
        Long driverId,
        String driverName,
        String driverDocument,
        Long vehicleId,
        String vehiclePlate,
        String vehicleBrand,
        String vehicleModel,
        LocalDateTime dispatchDate,
        String status,
        String notes,
        String dispatchUserName,
        List<DispatchDetailResponse> details,
        LocalDateTime createdAt
) {

    @Builder
    public record DispatchDetailResponse(
            Long id,
            Long productId,
            String productName,
            String productCode,
            BigDecimal quantity,
            BigDecimal delivered,
            String observations
    ) {}
}
