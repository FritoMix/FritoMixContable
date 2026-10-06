package com.fritomix.erp.security.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRateLimitFilterTest {

    private static final int MAX_ATTEMPTS = 2;

    private final LoginRateLimitFilter filter = new LoginRateLimitFilter(MAX_ATTEMPTS, 60);

    private MockHttpServletResponse call(String method, String uri) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> { };
        filter.doFilter(request, response, chain);
        return response;
    }

    private void exhaust(String uri) throws Exception {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            assertEquals(200, call("POST", uri).getStatus(), "se esperaba permitido para " + uri);
        }
    }

    @Test
    void shouldRejectLoginAfterMaxAttempts() throws Exception {
        exhaust("/api/v1/auth/login");

        MockHttpServletResponse blocked = call("POST", "/api/v1/auth/login");

        assertEquals(429, blocked.getStatus());
        assertNotNull(blocked.getHeader("Retry-After"));
        assertTrue(blocked.getContentAsString().contains("Demasiadas solicitudes"));
    }

    /**
     * Regresión del fix: antes solo se limitaba {@code /login}, dejando
     * {@code /verify-reset-code} sin freno para fuerza bruta de códigos.
     */
    @Test
    void shouldRateLimitVerifyResetCode() throws Exception {
        exhaust("/api/v1/auth/verify-reset-code");

        assertEquals(429, call("POST", "/api/v1/auth/verify-reset-code").getStatus());
    }

    @Test
    void shouldRateLimitResetPasswordAndForgotPassword() throws Exception {
        exhaust("/api/v1/auth/reset-password");
        assertEquals(429, call("POST", "/api/v1/auth/reset-password").getStatus());

        exhaust("/api/v1/auth/forgot-password");
        assertEquals(429, call("POST", "/api/v1/auth/forgot-password").getStatus());
    }

    /**
     * Buckets por endpoint: saturar login no debe impedir refrescar la sesión.
     */
    @Test
    void shouldKeepBucketsIndependentPerEndpoint() throws Exception {
        exhaust("/api/v1/auth/login");

        assertEquals(429, call("POST", "/api/v1/auth/login").getStatus());
        assertEquals(200, call("POST", "/api/v1/auth/refresh").getStatus());
        assertEquals(200, call("POST", "/api/v1/auth/logout").getStatus());
    }

    @Test
    void shouldNotFilterGetRequests() throws Exception {
        for (int i = 0; i < MAX_ATTEMPTS + 3; i++) {
            assertEquals(200, call("GET", "/api/v1/auth/login").getStatus());
        }
    }

    @Test
    void shouldNotFilterPathsOutsideAuth() throws Exception {
        for (int i = 0; i < MAX_ATTEMPTS + 3; i++) {
            assertEquals(200, call("POST", "/api/v1/orders").getStatus());
            assertEquals(200, call("POST", "/api/v1/authors").getStatus());
        }
    }

    @Test
    void shouldNotLeaveAlreadyFilteredMarkerWhenSkipped() throws Exception {
        MockHttpServletResponse response = call("POST", "/api/v1/orders");
        assertEquals(200, response.getStatus());
        assertNull(response.getHeader("Retry-After"));
    }
}
