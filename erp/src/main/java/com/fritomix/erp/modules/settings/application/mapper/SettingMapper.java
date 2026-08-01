package com.fritomix.erp.modules.settings.application.mapper;

import com.fritomix.erp.modules.settings.application.dto.response.SettingResponse;
import com.fritomix.erp.modules.settings.domain.entity.CompanySetting;
import org.springframework.stereotype.Component;

@Component
public class SettingMapper {

    public SettingResponse toResponse(CompanySetting setting) {
        return SettingResponse.builder()
                .id(setting.getId())
                .companyName(setting.getCompanyName())
                .nit(setting.getNit())
                .adminEmail(setting.getAdminEmail())
                .address(setting.getAddress())
                .phone(setting.getPhone())
                .city(setting.getCity())
                .department(setting.getDepartment())
                .economicActivity(setting.getEconomicActivity())
                .passwordMinLength(setting.getPasswordMinLength())
                .passwordRequireSpecial(setting.getPasswordRequireSpecial())
                .passwordExpirationDays(setting.getPasswordExpirationDays())
                .sessionTimeoutMinutes(setting.getSessionTimeoutMinutes())
                .maxLoginAttempts(setting.getMaxLoginAttempts())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }
}
