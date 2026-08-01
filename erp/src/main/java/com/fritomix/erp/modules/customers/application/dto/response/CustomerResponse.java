package com.fritomix.erp.modules.customers.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CustomerResponse(
        Long id,
        String code,
        String document,
        String businessName,
        String contactName,
        String phone,
        String email,
        Boolean active,
        String address,
        Long cityId,
        String cityName,
        Long departmentId,
        String departmentName,
        LocalDateTime createdAt
) {
}
