package com.fritomix.erp.modules.settings.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.settings.application.dto.request.SettingRequest;
import com.fritomix.erp.modules.settings.application.dto.response.SettingResponse;
import com.fritomix.erp.modules.settings.application.mapper.SettingMapper;
import com.fritomix.erp.modules.settings.domain.entity.CompanySetting;
import com.fritomix.erp.modules.settings.domain.repository.CompanySettingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingServiceTest {

    @Mock
    private CompanySettingRepository repository;

    @Mock
    private SettingMapper mapper;

    @InjectMocks
    private SettingService service;

    @Test
    void getEncontradoYNoEncontrado() {
        CompanySetting s = new CompanySetting();
        s.setCompanyName("FritoMix");
        when(repository.findAll()).thenReturn(List.of(s));
        when(mapper.toResponse(s)).thenReturn(mock(SettingResponse.class));

        assertNotNull(service.get());

        when(repository.findAll()).thenReturn(List.of());
        assertThrows(ResourceNotFoundException.class, () -> service.get());
    }

    @Test
    void getSecurityPolicyConValoresGuardados() {
        CompanySetting s = new CompanySetting();
        s.setPasswordMinLength(10);
        s.setPasswordRequireSpecial(false);
        s.setMaxLoginAttempts(3);
        s.setLockDurationMinutes(30);
        when(repository.findAll()).thenReturn(List.of(s));

        SettingService.SecurityPolicy policy = service.getSecurityPolicy();

        assertEquals(10, policy.passwordMinLength());
        assertFalse(policy.passwordRequireSpecial());
        assertEquals(3, policy.maxLoginAttempts());
        assertEquals(30, policy.lockDurationMinutes());
    }

    @Test
    void getSecurityPolicyAplicaDefaultsSiCamposNulos() {
        CompanySetting s = new CompanySetting();
        when(repository.findAll()).thenReturn(List.of(s));

        SettingService.SecurityPolicy policy = service.getSecurityPolicy();

        assertEquals(8, policy.passwordMinLength());
        assertTrue(policy.passwordRequireSpecial());
        assertEquals(5, policy.maxLoginAttempts());
        assertEquals(15, policy.lockDurationMinutes());
    }

    @Test
    void getSecurityPolicySinSettingsUsaDefaults() {
        when(repository.findAll()).thenReturn(List.of());

        SettingService.SecurityPolicy policy = service.getSecurityPolicy();

        assertEquals(8, policy.passwordMinLength());
        assertEquals(15, policy.lockDurationMinutes());
    }

    @Test
    void updateCreaNuevoSettingCuandoNoExiste() {
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(any(CompanySetting.class))).thenAnswer(inv -> {
            CompanySetting s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });
        when(mapper.toResponse(any(CompanySetting.class))).thenReturn(mock(SettingResponse.class));

        SettingRequest request = SettingRequest.builder()
                .companyName("FritoMix").nit("900").passwordMinLength(8).passwordRequireSpecial(true)
                .maxLoginAttempts(5).build();

        assertNotNull(service.update(request));
        verify(repository).save(any(CompanySetting.class));
    }

    @Test
    void updateCambiaSettingsExistentes() {
        CompanySetting s = new CompanySetting();
        s.setCompanyName("Vieja");
        s.setPasswordMinLength(6);
        when(repository.findAll()).thenReturn(List.of(s));
        when(repository.save(s)).thenReturn(s);
        when(mapper.toResponse(s)).thenReturn(mock(SettingResponse.class));

        SettingRequest request = SettingRequest.builder()
                .companyName("Nueva").passwordMinLength(12).passwordRequireSpecial(false)
                .maxLoginAttempts(8).build();

        service.update(request);

        assertEquals("Nueva", s.getCompanyName());
        assertEquals(12, s.getPasswordMinLength());
        verify(repository).save(s);
    }
}