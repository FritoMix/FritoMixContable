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

    private static final String LOGIN_PATH = "/api/v1/auth/login";

    private final FixedWindowRateLimiter rateLimiter;

    public LoginRateLimitFilter(int maxAttempts, long windowSeconds) {
        this.rateLimiter = new FixedWindowRateLimiter(maxAttempts, windowSeconds * 1000);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !(request.getRequestURI().startsWith(LOGIN_PATH)
                && "POST".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientIp = resolveClientIp(request);
        long retryAfterSeconds = rateLimiter.retryAfterSeconds(clientIp);
        if (retryAfterSeconds > 0) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.getWriter().write(
                    "{\"timestamp\":\"" + LocalDateTime.now()
                            + "\",\"status\":429,\"error\":\"Demasiados intentos de inicio de sesión. Intenta de nuevo en "
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