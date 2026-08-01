package com.fritomix.erp.modules.settings.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SettingResponse(
        Long id,
        String companyName,
        String nit,
        String adminEmail,
        String address,
        String phone,
        String city,
        String department,
        String economicActivity,
        Integer passwordMinLength,
        Boolean passwordRequireSpecial,
        Integer passwordExpirationDays,
        Integer sessionTimeoutMinutes,
        Integer maxLoginAttempts,
        LocalDateTime updatedAt
) {
}
