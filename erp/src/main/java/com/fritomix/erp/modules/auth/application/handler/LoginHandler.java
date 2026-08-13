package com.fritomix.erp.modules.auth.application.handler;

import com.fritomix.erp.modules.auth.application.command.LoginCommand;
import com.fritomix.erp.modules.auth.domain.entity.RefreshToken;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.RefreshTokenRepository;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.auth.exception.AccountLockedException;
import com.fritomix.erp.modules.auth.exception.InvalidCredentialsException;
import com.fritomix.erp.modules.auth.exception.UserDisabledException;
import com.fritomix.erp.modules.settings.application.service.SettingService;
import com.fritomix.erp.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginHandler {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SettingService settingService;

    @Transactional
    public TokenResult handle(LoginCommand command) {
        User user = userRepository.findByEmail(command.email()).orElse(null);

        if (user != null && Boolean.FALSE.equals(user.getAccountNonLocked())) {
            if (user.getLockedUntil() != null && user.getLockedUntil().isBefore(LocalDateTime.now())) {
                user.setAccountNonLocked(true);
                user.setFailedAttempts(0);
                user.setLockedUntil(null);
            } else {
                throw new AccountLockedException("La cuenta está bloqueada por demasiados intentos fallidos");
            }
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            command.email(),
                            command.password()
                    )
            );
        } catch (BadCredentialsException e) {
            registerFailedAttempt(user);
            throw new InvalidCredentialsException("Correo o contraseña incorrectos");
        } catch (DisabledException e) {
            throw new UserDisabledException("La cuenta de usuario está deshabilitada");
        } catch (LockedException e) {
            throw new AccountLockedException("La cuenta está bloqueada por demasiados intentos fallidos");
        }

        if (user == null) {
            user = userRepository.findByEmail(command.email())
                    .orElseThrow(() -> new InvalidCredentialsException("Correo o contraseña incorrectos"));
        }

        user.setFailedAttempts(0);
        user.setAccountNonLocked(true);
        user.setLockedUntil(null);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenRepository.findByUser(user).forEach(t -> t.setRevoked(true));

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiresAt(jwtService.extractExpiration(refreshToken).toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return new TokenResult(user, accessToken, refreshToken);
    }

    private void registerFailedAttempt(User user) {
        if (user == null) {
            return;
        }
        SettingService.SecurityPolicy policy = settingService.getSecurityPolicy();
        int attempts = (user.getFailedAttempts() == null ? 0 : user.getFailedAttempts()) + 1;
        user.setFailedAttempts(attempts);
        if (attempts >= policy.maxLoginAttempts()) {
            user.setAccountNonLocked(false);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(policy.lockDurationMinutes()));
        }
        userRepository.save(user);
    }

    public record TokenResult(User user, String accessToken, String refreshToken) {}
}