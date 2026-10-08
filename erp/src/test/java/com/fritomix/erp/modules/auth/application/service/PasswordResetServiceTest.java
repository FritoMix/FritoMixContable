package com.fritomix.erp.modules.auth.application.service;

import com.fritomix.erp.modules.auth.domain.entity.PasswordResetCode;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.PasswordResetCodeRepository;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.auth.exception.InvalidPasswordResetCodeException;
import com.fritomix.erp.modules.auth.exception.UserNotFoundException;
import com.fritomix.erp.modules.notifications.application.service.EmailService;
import com.fritomix.erp.modules.notifications.application.service.EmailTemplateBuilder;
import com.fritomix.erp.modules.settings.application.service.SettingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetCodeRepository resetCodeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private SettingService settingService;

    @Spy
    private EmailTemplateBuilder emailTemplateBuilder = new EmailTemplateBuilder();

    @InjectMocks
    private PasswordResetService service;

    private User user() {
        return User.builder().id(1L).email("ana@x.com").firstName("Ana").lastName("Soto").build();
    }

    private PasswordResetCode validCode() {
        return PasswordResetCode.builder()
                .email("ana@x.com").codeHash("hash")
                .expiresAt(LocalDateTime.now().plusMinutes(10)).used(false)
                .build();
    }

    @Test
    void requestResetCorreoInexistenteNoEnviaNada() {
        when(userRepository.findByEmail("nadie@x.com")).thenReturn(Optional.empty());

        service.requestReset("  Nadie@x.com ");

        verify(resetCodeRepository, never()).save(any());
        verify(emailService, never()).sendHtmlEmail(anyString(), anyString(), anyString());
    }

    @Test
    void requestResetGeneraCodigoYEnviaCorreo() {
        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user()));
        when(passwordEncoder.encode(anyString())).thenReturn("hash");

        service.requestReset("Ana@x.com");

        verify(resetCodeRepository).save(any(PasswordResetCode.class));
        verify(emailService).sendHtmlEmail(eq("ana@x.com"), anyString(), anyString());
    }

    @Test
    void verifyCodeExitoso() {
        when(resetCodeRepository.findFirstByEmailAndUsedFalseOrderByIdDesc("ana@x.com"))
                .thenReturn(Optional.of(validCode()));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);

        service.verifyCode("ana@x.com", "123456");
    }

    @Test
    void verifyCodeSinCodigoLanzaExcepcion() {
        when(resetCodeRepository.findFirstByEmailAndUsedFalseOrderByIdDesc("ana@x.com"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidPasswordResetCodeException.class,
                () -> service.verifyCode("ana@x.com", "000000"));
    }

    @Test
    void verifyCodeExpiradoLanzaExcepcion() {
        PasswordResetCode expired = PasswordResetCode.builder()
                .email("ana@x.com").codeHash("hash")
                .expiresAt(LocalDateTime.now().minusMinutes(1)).used(false).build();
        when(resetCodeRepository.findFirstByEmailAndUsedFalseOrderByIdDesc("ana@x.com"))
                .thenReturn(Optional.of(expired));

        assertThrows(InvalidPasswordResetCodeException.class,
                () -> service.verifyCode("ana@x.com", "123456"));
    }

    @Test
    void verifyCodeIncorrectoLanzaExcepcion() {
        when(resetCodeRepository.findFirstByEmailAndUsedFalseOrderByIdDesc("ana@x.com"))
                .thenReturn(Optional.of(validCode()));
        when(passwordEncoder.matches("999999", "hash")).thenReturn(false);

        assertThrows(InvalidPasswordResetCodeException.class,
                () -> service.verifyCode("ana@x.com", "999999"));
    }

    @Test
    void resetPasswordExitoso() {
        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user()));
        when(resetCodeRepository.findFirstByEmailAndUsedFalseOrderByIdDesc("ana@x.com"))
                .thenReturn(Optional.of(validCode()));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);
        when(settingService.getSecurityPolicy()).thenReturn(new SettingService.SecurityPolicy(8, false, 5));
        when(passwordEncoder.encode("nuevaClave1")).thenReturn("nuevoHash");

        service.resetPassword("ana@x.com", "123456", "nuevaClave1");

        verify(resetCodeRepository).save(any(PasswordResetCode.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void resetPasswordUsuarioInexistenteLanzaExcepcion() {
        when(userRepository.findByEmail("nadie@x.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.resetPassword("nadie@x.com", "123456", "nuevaClave1"));
    }

    @Test
    void resetPasswordMuyCortaLanzaExcepcion() {
        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user()));
        when(resetCodeRepository.findFirstByEmailAndUsedFalseOrderByIdDesc("ana@x.com"))
                .thenReturn(Optional.of(validCode()));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);
        when(settingService.getSecurityPolicy()).thenReturn(new SettingService.SecurityPolicy(10, false, 5));

        assertThrows(IllegalArgumentException.class,
                () -> service.resetPassword("ana@x.com", "123456", "corta"));
    }

    @Test
    void resetPasswordSinCaracterEspecialLanzaExcepcion() {
        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user()));
        when(resetCodeRepository.findFirstByEmailAndUsedFalseOrderByIdDesc("ana@x.com"))
                .thenReturn(Optional.of(validCode()));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);
        when(settingService.getSecurityPolicy()).thenReturn(new SettingService.SecurityPolicy(8, true, 5));

        assertThrows(IllegalArgumentException.class,
                () -> service.resetPassword("ana@x.com", "123456", "sololetras"));
    }
}