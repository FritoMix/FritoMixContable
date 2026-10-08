package com.fritomix.erp.modules.auth.api;

import com.fritomix.erp.modules.auth.application.command.LoginCommand;
import com.fritomix.erp.modules.auth.application.command.RefreshTokenCommand;
import com.fritomix.erp.modules.auth.application.dto.request.ForgotPasswordRequest;
import com.fritomix.erp.modules.auth.application.dto.request.LoginRequest;
import com.fritomix.erp.modules.auth.application.dto.request.ResetPasswordRequest;
import com.fritomix.erp.modules.auth.application.dto.request.VerifyResetCodeRequest;
import com.fritomix.erp.modules.auth.application.dto.response.AuthenticationResponse;
import com.fritomix.erp.modules.auth.application.service.AuthenticationService;
import com.fritomix.erp.modules.auth.application.service.PasswordResetService;
import com.fritomix.erp.security.jwt.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "cookieSecure", false);
        ReflectionTestUtils.setField(controller, "cookieSameSite", "None");
    }

    private AuthenticationResponse authResponse() {
        return AuthenticationResponse.builder()
                .accessToken("access").refreshToken("refresh")
                .firstName("Ana").lastName("Soto").email("a@x.com").role("ADMIN").build();
    }

    @Test
    void loginSeteaCookieRefreshToken() {
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(3_600_000L);
        when(authenticationService.login(org.mockito.ArgumentMatchers.any(LoginCommand.class))).thenReturn(authResponse());
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<AuthenticationResponse> result = controller.login(
                LoginRequest.builder().email("a@x.com").password("pass").build(), response);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        String cookie = response.getHeader(HttpHeaders.SET_COOKIE);
        assertNotNull(cookie);
        assertTrue(cookie.contains("refreshToken=refresh"));
    }

    @Test
    void refreshSeteaCookie() {
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(3_600_000L);
        when(authenticationService.refresh(org.mockito.ArgumentMatchers.any(RefreshTokenCommand.class))).thenReturn(authResponse());
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<AuthenticationResponse> result = controller.refresh("old-refresh", response);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(response.getHeader(HttpHeaders.SET_COOKIE));
    }

    @Test
    void refreshSinTokenLanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> controller.refresh(null, new MockHttpServletResponse()));
    }

    @Test
    void logoutConTokenRevocaYLimpiaCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<Void> result = controller.logout("some-refresh", response);

        assertEquals(204, result.getStatusCode().value());
        verify(authenticationService).revoke("some-refresh");
        assertNotNull(response.getHeader(HttpHeaders.SET_COOKIE));
    }

    @Test
    void logoutSinTokenNoRevoca() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<Void> result = controller.logout(null, response);

        assertEquals(204, result.getStatusCode().value());
        assertNotNull(response.getHeader(HttpHeaders.SET_COOKIE));
    }

    @Test
    void forgotPasswordDelegaAlServicio() {
        controller.forgotPassword(new ForgotPasswordRequest("a@x.com"));

        verify(passwordResetService).requestReset("a@x.com");
    }

    @Test
    void verifyResetCodeDelegaAlServicio() {
        controller.verifyResetCode(new VerifyResetCodeRequest("a@x.com", "123456"));

        verify(passwordResetService).verifyCode("a@x.com", "123456");
    }

    @Test
    void resetPasswordDelegaAlServicio() {
        controller.resetPassword(new ResetPasswordRequest("a@x.com", "123456", "NuevaPass1!"));

        verify(passwordResetService).resetPassword("a@x.com", "123456", "NuevaPass1!");
    }
}