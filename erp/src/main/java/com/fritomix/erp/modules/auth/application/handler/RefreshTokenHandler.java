package com.fritomix.erp.modules.auth.application.handler;

import com.fritomix.erp.modules.auth.application.command.RefreshTokenCommand;
import com.fritomix.erp.modules.auth.domain.entity.RefreshToken;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.RefreshTokenRepository;
import com.fritomix.erp.modules.auth.exception.RefreshTokenExpiredException;
import com.fritomix.erp.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefreshTokenHandler {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginHandler.TokenResult handle(RefreshTokenCommand command) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(command.refreshToken())
                .orElseThrow(() -> new RefreshTokenExpiredException("Refresh token inválido"));

        if (storedToken.getRevoked()) {
            throw new RefreshTokenExpiredException("Refresh token ya fue revocado");
        }

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new RefreshTokenExpiredException("Refresh token expirado");
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        User user = storedToken.getUser();

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        RefreshToken newTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .user(user)
                .expiresAt(jwtService.extractExpiration(newRefreshToken).toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                .revoked(false)
                .build();
        refreshTokenRepository.save(newTokenEntity);

        return new LoginHandler.TokenResult(user, newAccessToken, newRefreshToken);
    }

    @Transactional
    public void revoke(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(storedToken -> {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
        });
    }
}
