package com.fritomix.erp.modules.auth.application.service;

import com.fritomix.erp.modules.auth.domain.entity.PasswordResetCode;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.PasswordResetCodeRepository;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.auth.exception.InvalidPasswordResetCodeException;
import com.fritomix.erp.modules.auth.exception.UserNotFoundException;
import com.fritomix.erp.modules.notifications.application.service.EmailService;
import com.fritomix.erp.modules.settings.application.service.SettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int CODE_LENGTH = 6;
    private static final long CODE_TTL_MINUTES = 15;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^A-Za-z0-9]");

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository resetCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SettingService settingService;

    /**
     * Solicita un código de restablecimiento. No revela si el correo existe
     * para evitar enumeración de usuarios: siempre responde de forma genérica.
     */
    @Transactional
    public void requestReset(String email) {
        String normalized = email.trim().toLowerCase();

        if (!userRepository.findByEmail(normalized).isPresent()) {
            log.info("Solicitud de reset para correo inexistente: {}", normalized);
            return;
        }

        String code = generateCode();

        PasswordResetCode entity = PasswordResetCode.builder()
                .email(normalized)
                .codeHash(passwordEncoder.encode(code))
                .expiresAt(java.time.LocalDateTime.now().plusMinutes(CODE_TTL_MINUTES))
                .used(false)
                .build();
        resetCodeRepository.save(entity);

        emailService.sendEmail(
                normalized,
                "Código para restablecer tu contraseña - FritoMix",
                buildEmailBody(code)
        );
    }

    @Transactional(readOnly = true)
    public void verifyCode(String email, String code) {
        String normalized = email.trim().toLowerCase();
        PasswordResetCode resetCode = findValidCode(normalized, code);
        // El código es válido; no se marca como usado hasta el reset efectivo.
        log.debug("Código verificado correctamente para {}", normalized);
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        String normalized = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalized)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + normalized));

        PasswordResetCode resetCode = findValidCode(normalized, code);

        validatePasswordPolicy(newPassword);

        resetCode.setUsed(true);
        resetCodeRepository.save(resetCode);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Contraseña restablecida para {}", normalized);
    }

    private PasswordResetCode findValidCode(String email, String rawCode) {
        PasswordResetCode resetCode = resetCodeRepository
                .findFirstByEmailAndUsedFalseOrderByIdDesc(email)
                .orElseThrow(() -> new InvalidPasswordResetCodeException("El código no es válido o ha expirado"));

        if (resetCode.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new InvalidPasswordResetCodeException("El código ha expirado. Solicita uno nuevo.");
        }

        if (!passwordEncoder.matches(rawCode, resetCode.getCodeHash())) {
            throw new InvalidPasswordResetCodeException("El código es incorrecto.");
        }

        return resetCode;
    }

    private void validatePasswordPolicy(String password) {
        SettingService.SecurityPolicy policy = settingService.getSecurityPolicy();
        if (password == null || password.length() < policy.passwordMinLength()) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos " + policy.passwordMinLength() + " caracteres");
        }
        if (policy.passwordRequireSpecial() && !SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un carácter especial");
        }
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private String buildEmailBody(String code) {
        return "Hola,\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña de FritoMix. "
                + "Utiliza el siguiente código para continuar:\n\n"
                + code + "\n\n"
                + "El código es válido por 15 minutos. Si no lo solicitaste, ignora este correo.\n\n"
                + "Gracias,\nEquipo FritoMix";
    }
}
