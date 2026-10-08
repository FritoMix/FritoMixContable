package com.fritomix.erp.security.jwt;

import com.fritomix.erp.modules.auth.application.dto.JwtUserInfo;
import com.fritomix.erp.modules.auth.domain.entity.Permission;
import com.fritomix.erp.modules.auth.domain.entity.Role;
import com.fritomix.erp.modules.auth.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private HttpServletRequest request;

    private JwtService service;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=");
        props.setAccessTokenExpiration(3_600_000L);
        props.setRefreshTokenExpiration(86_400_000L);
        service = new JwtService(props);
    }

    private User user() {
        Permission perm = Permission.builder().id(1L).name("users:read").build();
        Role role = Role.builder().id(1L).name("ADMIN").permissions(List.of(perm)).build();
        return User.builder()
                .id(7L).email("ana@x.com").firstName("Ana").lastName("Soto").role(role)
                .build();
    }

    @Test
    void generaYExtraeInformacionDelAccessToken() {
        String token = service.generateAccessToken(user());

        JwtUserInfo info = service.extractUserInfo(token);

        assertEquals(7L, info.userId());
        assertEquals("ana@x.com", info.email());
        assertEquals("ADMIN", info.role());
        assertEquals("Ana", info.firstName());
        assertEquals("Soto", info.lastName());
        assertEquals(List.of("users:read"), info.permissions());
    }

    @Test
    void generateRefreshTokenProduceTokenValido() {
        String token = service.generateRefreshToken(user());

        assertNotNull(service.extractExpiration(token));
    }

    @Test
    void extractExpirationEsFutura() {
        String token = service.generateAccessToken(user());

        assertTrue(service.extractExpiration(token).after(new Date()));
    }

    @Test
    void extractClaimUsaResolver() {
        String token = service.generateAccessToken(user());

        assertEquals("ana@x.com", service.extractClaim(token, io.jsonwebtoken.Claims::getSubject));
    }

    @Test
    void extractTokenSinCabeceraDevuelveNull() {
        when(request.getHeader("Authorization")).thenReturn(null);

        assertNull(service.extractToken(request));
    }

    @Test
    void extractTokenConEsquemaInvalidoDevuelveNull() {
        when(request.getHeader("Authorization")).thenReturn("Basic abc");

        assertNull(service.extractToken(request));
    }

    @Test
    void extractTokenBearerDevuelveToken() {
        when(request.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");

        assertEquals("abc.def.ghi", service.extractToken(request));
    }
}