package com.fritomix.erp.modules.settings.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.settings.application.dto.request.SettingRequest;
import com.fritomix.erp.modules.settings.application.dto.response.SettingResponse;
import com.fritomix.erp.modules.settings.application.mapper.SettingMapper;
import com.fritomix.erp.modules.settings.domain.entity.CompanySetting;
import com.fritomix.erp.modules.settings.domain.repository.CompanySettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final CompanySettingRepository repository;
    private final SettingMapper mapper;

    @Transactional(readOnly = true)
    public SettingResponse get() {
        CompanySetting setting = repository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Configuración de empresa no encontrada"));
        return mapper.toResponse(setting);
    }

    @Transactional(readOnly = true)
    public SecurityPolicy getSecurityPolicy() {
        return repository.findAll().stream()
                .findFirst()
                .map(s -> new SecurityPolicy(
                        s.getPasswordMinLength() != null ? s.getPasswordMinLength() : 8,
                        s.getPasswordRequireSpecial() != null ? s.getPasswordRequireSpecial() : true,
                        s.getMaxLoginAttempts() != null ? s.getMaxLoginAttempts() : 5,
                        s.getLockDurationMinutes() != null ? s.getLockDurationMinutes() : 15))
                .orElseGet(SecurityPolicy::new);
    }

    public record SecurityPolicy(int passwordMinLength, boolean passwordRequireSpecial, int maxLoginAttempts, int lockDurationMinutes) {
        public SecurityPolicy(int passwordMinLength, boolean passwordRequireSpecial, int maxLoginAttempts) {
            this(passwordMinLength, passwordRequireSpecial, maxLoginAttempts, 15);
        }

        public SecurityPolicy() {
            this(8, true, 5, 15);
        }
    }

    @Transactional
    public SettingResponse update(SettingRequest request) {
        CompanySetting setting = repository.findAll().stream()
                .findFirst()
                .orElseGet(CompanySetting::new);

        setting.setCompanyName(request.companyName());
        setting.setNit(request.nit());
        setting.setAdminEmail(request.adminEmail());
        setting.setAddress(request.address());
        setting.setPhone(request.phone());
        setting.setCity(request.city());
        setting.setDepartment(request.department());
        setting.setEconomicActivity(request.economicActivity());
        setting.setPasswordMinLength(request.passwordMinLength());
        setting.setPasswordRequireSpecial(request.passwordRequireSpecial());
        setting.setPasswordExpirationDays(request.passwordExpirationDays());
        setting.setSessionTimeoutMinutes(request.sessionTimeoutMinutes());
        setting.setMaxLoginAttempts(request.maxLoginAttempts());

        setting = repository.save(setting);
        return mapper.toResponse(setting);
    }
}
