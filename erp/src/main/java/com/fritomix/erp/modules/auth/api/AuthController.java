package com.fritomix.erp.modules.auth.api;

import com.fritomix.erp.modules.auth.application.command.LoginCommand;
import com.fritomix.erp.modules.auth.application.command.RefreshTokenCommand;
import com.fritomix.erp.modules.auth.application.dto.request.LoginRequest;
import com.fritomix.erp.modules.auth.application.dto.response.AuthenticationResponse;
import com.fritomix.erp.modules.auth.application.service.AuthenticationService;
import com.fritomix.erp.modules.auth.exception.RefreshTokenExpiredException;
import com.fritomix.erp.security.jwt.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final AuthenticationService authenticationService;
    private final JwtProperties jwtProperties;

    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:none}")
    private String cookieSameSite;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request,
                                                        HttpServletResponse response) {
        LoginCommand command = LoginCommand.builder()
                .email(request.email())
                .password(request.password())
                .build();
        AuthenticationResponse authResponse = authenticationService.login(command);
        setRefreshTokenCookie(response, authResponse.refreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RefreshTokenExpiredException("Refresh token inválido o ausente");
        }
        RefreshTokenCommand command = RefreshTokenCommand.builder()
                .refreshToken(refreshToken)
                .build();
        AuthenticationResponse authResponse = authenticationService.refresh(command);
        setRefreshTokenCookie(response, authResponse.refreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            authenticationService.revoke(refreshToken);
        }
        clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        long maxAgeSeconds = jwtProperties.getRefreshTokenExpiration() / 1000;
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/api/v1/auth")
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/api/v1/auth")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
