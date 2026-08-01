package com.fritomix.erp.modules.vehicles.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record VehicleResponse(
        Long id,
        String plate,
        String brand,
        String model,
        BigDecimal capacity,
        Boolean active,
        LocalDateTime createdAt
) {
}
