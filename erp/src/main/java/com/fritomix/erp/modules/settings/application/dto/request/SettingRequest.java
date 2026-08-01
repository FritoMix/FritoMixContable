package com.fritomix.erp.modules.settings.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SettingRequest(
        @NotBlank @Size(max = 200)
        String companyName,

        @Size(max = 50)
        String nit,

        @Size(max = 150)
        String adminEmail,

        @Size(max = 250)
        String address,

        @Size(max = 30)
        String phone,

        @Size(max = 100)
        String city,

        @Size(max = 100)
        String department,

        @Size(max = 200)
        String economicActivity,

        @NotNull
        Integer passwordMinLength,

        @NotNull
        Boolean passwordRequireSpecial,

        @NotNull
        Integer passwordExpirationDays,

        @NotNull
        Integer sessionTimeoutMinutes,

        @NotNull
        Integer maxLoginAttempts
) {
}
