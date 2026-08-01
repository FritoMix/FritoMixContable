package com.fritomix.erp.modules.drivers.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DriverResponse(
        Long id,
        String document,
        String name,
        String phone,
        String licenseNumber,
        Boolean active,
        LocalDateTime createdAt
) {
}
