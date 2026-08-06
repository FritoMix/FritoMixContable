package com.fritomix.erp.modules.vehicles.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record VehicleResponse(
        Long id,
        String vehicleNumber,
        String type,
        BigDecimal capacity,
        BigDecimal dimension,
        Boolean active,
        LocalDateTime createdAt
) {
}
