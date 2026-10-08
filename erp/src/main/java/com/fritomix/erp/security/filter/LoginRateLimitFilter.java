package com.fritomix.erp.security.filter;

import com.fritomix.erp.security.ratelimit.FixedWindowRateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LoginRateLimitFilter extends OncePerRequestFilter {

    /**
     * Todo lo que vive bajo {@code /api/v1/auth/} expone operaciones costosas o
     * adivinables: login, refresh, forgot-password, verify-reset-code y
     * reset-password. Sin límite, un atacante podía fuerza-brutar el código de
     * 6 dígitos o provocar consumo de CPU ilimitado con intentos de BCrypt.
     */
    private static final String AUTH_PATH_PREFIX = "/api/v1/auth/";

    private final FixedWindowRateLimiter rateLimiter;

    public LoginRateLimitFilter(int maxAttempts, long windowSeconds) {
        this.rateLimiter = new FixedWindowRateLimiter(maxAttempts, windowSeconds * 1000);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().startsWith(AUTH_PATH_PREFIX));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Bucket por endpoint e IP: que /login se sature no debe impedir /refresh,
        // y viceversa. Cada endpoint sigue quedando limitado individualmente.
        String key = request.getRequestURI() + "|" + resolveClientIp(request);
        long retryAfterSeconds = rateLimiter.retryAfterSeconds(key);
        if (retryAfterSeconds > 0) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.getWriter().write(
                    "{\"timestamp\":\"" + LocalDateTime.now()
                            + "\",\"status\":429,\"error\":\"Demasiadas solicitudes. Intenta de nuevo en "
                            + retryAfterSeconds + " segundos.\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}