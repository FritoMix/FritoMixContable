package com.fritomix.erp.security.jwt;

import com.fritomix.erp.modules.auth.application.dto.JwtUserInfo;
import com.fritomix.erp.modules.auth.domain.entity.Permission;
import com.fritomix.erp.modules.auth.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    /**
     * Obtiene la clave utilizada para firmar los JWT.
     */
    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());

        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * ============================================================
     * GENERACIÓN DE TOKENS
     * ============================================================
     */

    public String generateAccessToken(User user) {

        return buildToken(
                user,
                properties.getAccessTokenExpiration()
        );
    }

    public String generateRefreshToken(User user) {

        return buildToken(
                user,
                properties.getRefreshTokenExpiration()
        );
    }

    /**
     * Construye el JWT.
     */
    private String buildToken(
            User user,
            Long expiration
    ) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getId());
        claims.put("role", user.getRole().getName());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        List<String> permissions = user.getRole().getPermissions().stream()
                .map(Permission::getName)
                .toList();
        claims.put("permissions", permissions);
        claims.put("jti", UUID.randomUUID().toString());

        Date now = new Date();

        Date expirationDate = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * ============================================================
     * EXTRACCIÓN DE INFORMACIÓN
     * ============================================================
     */

    @SuppressWarnings("unchecked")
    public JwtUserInfo extractUserInfo(String token) {

        Claims claims = extractAllClaims(token);

        List<String> permissions = claims.get("permissions", ArrayList.class);
        if (permissions == null) {
            permissions = List.of();
        }

        return new JwtUserInfo(
                claims.get("userId", Long.class),
                claims.getSubject(),
                claims.get("role", String.class),
                claims.get("firstName", String.class),
                claims.get("lastName", String.class),
                permissions
        );
    }

    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * Lee todos los claims del JWT.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * ============================================================
     * VALIDACIONES
     * ============================================================
     */

    public String extractToken(HttpServletRequest request){
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer")) {
            return null;
        }
        return  header.substring(7);
    }

}