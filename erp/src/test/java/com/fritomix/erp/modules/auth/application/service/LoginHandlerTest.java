package com.fritomix.erp.modules.auth.application.service;

import com.fritomix.erp.modules.auth.application.command.LoginCommand;
import com.fritomix.erp.modules.auth.application.handler.LoginHandler;
import com.fritomix.erp.modules.auth.domain.entity.RefreshToken;
import com.fritomix.erp.modules.auth.domain.entity.Role;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.RefreshTokenRepository;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.auth.exception.AccountLockedException;
import com.fritomix.erp.modules.auth.exception.InvalidCredentialsException;
import com.fritomix.erp.modules.auth.exception.UserDisabledException;
import com.fritomix.erp.modules.settings.application.service.SettingService;
import com.fritomix.erp.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginHandlerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private SettingService settingService;

    @InjectMocks
    private LoginHandler loginHandler;

    private LoginCommand validCommand;
    private User validUser;

    @BeforeEach
    void setUp() {
        validCommand = LoginCommand.builder()
                .email("admin@test.com")
                .password("password123")
                .build();

        validUser = User.builder()
                .id(1L)
                .email("admin@test.com")
                .firstName("Admin")
                .lastName("Test")
                .password("encoded-password")
                .role(Role.builder().name("ADMIN").build())
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .build();
    }

    @Test
    void handle_shouldSucceedWithValidCredentials() {
        when(userRepository.findByEmail(validCommand.email())).thenReturn(Optional.of(validUser));
        when(jwtService.generateAccessToken(validUser)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(validUser)).thenReturn("refresh-token");
        when(jwtService.extractExpiration("refresh-token")).thenReturn(java.util.Date.from(java.time.Instant.now().plusSeconds(3600)));
        when(refreshTokenRepository.findByUser(validUser)).thenReturn(List.of());

        LoginHandler.TokenResult result = loginHandler.handle(validCommand);

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals(validUser, result.user());
        assertEquals(0, result.user().getFailedAttempts());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(validCommand.email());
        verify(userRepository).save(validUser);
    }

    @Test
    void handle_shouldThrowInvalidCredentialsWhenBadPassword() {
        when(userRepository.findByEmail(validCommand.email())).thenReturn(Optional.of(validUser));
        when(settingService.getSecurityPolicy())
                .thenReturn(new SettingService.SecurityPolicy(8, true, 5));
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(InvalidCredentialsException.class, () -> loginHandler.handle(validCommand));
        assertEquals(1, validUser.getFailedAttempts());
        verify(userRepository).findByEmail(validCommand.email());
        verify(userRepository).save(validUser);
    }

    @Test
    void handle_shouldThrowUserDisabledWhenAccountDisabled() {
        when(userRepository.findByEmail(validCommand.email())).thenReturn(Optional.of(validUser));
        doThrow(new DisabledException("User disabled"))
                .when(authenticationManager).authenticate(any());

        assertThrows(UserDisabledException.class, () -> loginHandler.handle(validCommand));
        verify(userRepository).findByEmail(validCommand.email());
    }

    @Test
    void handle_shouldThrowAccountLockedWhenLockedThresholdReached() {
        validUser.setFailedAttempts(4);
        when(userRepository.findByEmail(validCommand.email())).thenReturn(Optional.of(validUser));
        when(settingService.getSecurityPolicy())
                .thenReturn(new SettingService.SecurityPolicy(8, true, 5));
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(InvalidCredentialsException.class, () -> loginHandler.handle(validCommand));
        assertEquals(5, validUser.getFailedAttempts());
        assertFalse(validUser.getAccountNonLocked());
        assertNotNull(validUser.getLockedUntil());
        assertTrue(validUser.getLockedUntil().isAfter(LocalDateTime.now()));
    }

    @Test
    void handle_shouldThrowAccountLockedWhenAlreadyLocked() {
        validUser.setAccountNonLocked(false);
        validUser.setLockedUntil(LocalDateTime.now().plusMinutes(10));
        when(userRepository.findByEmail(validCommand.email())).thenReturn(Optional.of(validUser));

        assertThrows(AccountLockedException.class, () -> loginHandler.handle(validCommand));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void handle_shouldAutoUnlockWhenLockWindowExpired() {
        validUser.setAccountNonLocked(false);
        validUser.setFailedAttempts(5);
        validUser.setLockedUntil(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByEmail(validCommand.email())).thenReturn(Optional.of(validUser));
        when(jwtService.generateAccessToken(validUser)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(validUser)).thenReturn("refresh-token");
        when(jwtService.extractExpiration("refresh-token")).thenReturn(java.util.Date.from(java.time.Instant.now().plusSeconds(3600)));
        when(refreshTokenRepository.findByUser(validUser)).thenReturn(List.of());

        LoginHandler.TokenResult result = loginHandler.handle(validCommand);

        assertNotNull(result);
        assertTrue(validUser.getAccountNonLocked());
        assertEquals(0, validUser.getFailedAttempts());
        assertNull(validUser.getLockedUntil());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void handle_shouldThrowInvalidCredentialsWhenUserNotFound() {
        when(userRepository.findByEmail(validCommand.email())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> loginHandler.handle(validCommand));
    }
}
