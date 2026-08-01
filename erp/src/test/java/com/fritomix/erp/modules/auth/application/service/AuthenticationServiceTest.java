package com.fritomix.erp.modules.auth.application.service;

import com.fritomix.erp.modules.auth.application.command.LoginCommand;
import com.fritomix.erp.modules.auth.application.dto.response.AuthenticationResponse;
import com.fritomix.erp.modules.auth.application.handler.LoginHandler;
import com.fritomix.erp.modules.auth.application.mapper.AuthenticationMapper;
import com.fritomix.erp.modules.auth.domain.entity.Role;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.exception.InvalidCredentialsException;
import com.fritomix.erp.modules.auth.exception.UserDisabledException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private LoginHandler loginHandler;

    @Mock
    private AuthenticationMapper mapper;

    @InjectMocks
    private AuthenticationService authenticationService;

    private LoginCommand validCommand;

    @BeforeEach
    void setUp() {
        validCommand = LoginCommand.builder()
                .email("admin@test.com")
                .password("password123")
                .build();
    }

    @Test
    void login_shouldSucceedWithValidCredentials() {
        User user = User.builder()
                .id(1L)
                .email("admin@test.com")
                .firstName("Admin")
                .lastName("Test")
                .role(Role.builder().name("ADMIN").build())
                .build();

        LoginHandler.TokenResult tokenResult = new LoginHandler.TokenResult(user, "access-token", "refresh-token");
        when(loginHandler.handle(validCommand)).thenReturn(tokenResult);
        when(mapper.toAuthenticationResponse("access-token", "refresh-token", user))
                .thenReturn(AuthenticationResponse.builder()
                        .accessToken("access-token")
                        .refreshToken("refresh-token")
                        .build());

        AuthenticationResponse response = authenticationService.login(validCommand);

        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        verify(loginHandler).handle(validCommand);
    }

    @Test
    void login_shouldThrowInvalidCredentialsWhenBadCredentials() {
        when(loginHandler.handle(any())).thenThrow(new InvalidCredentialsException("Correo o contraseña incorrectos"));

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(validCommand));
    }

    @Test
    void login_shouldThrowUserDisabledWhenAccountDisabled() {
        when(loginHandler.handle(any())).thenThrow(new UserDisabledException("La cuenta de usuario está deshabilitada"));

        assertThrows(UserDisabledException.class, () -> authenticationService.login(validCommand));
    }
}
