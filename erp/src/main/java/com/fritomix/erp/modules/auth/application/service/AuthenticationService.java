package com.fritomix.erp.modules.auth.application.service;

import com.fritomix.erp.modules.auth.application.command.LoginCommand;
import com.fritomix.erp.modules.auth.application.command.RefreshTokenCommand;
import com.fritomix.erp.modules.auth.application.dto.response.AuthenticationResponse;
import com.fritomix.erp.modules.auth.application.handler.LoginHandler;
import com.fritomix.erp.modules.auth.application.handler.RefreshTokenHandler;
import com.fritomix.erp.modules.auth.application.mapper.AuthenticationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final LoginHandler loginHandler;
    private final RefreshTokenHandler refreshTokenHandler;
    private final AuthenticationMapper mapper;

    public AuthenticationResponse login(LoginCommand command) {
        LoginHandler.TokenResult result = loginHandler.handle(command);
        return mapper.toAuthenticationResponse(result.accessToken(), result.refreshToken(), result.user());
    }

    public AuthenticationResponse refresh(RefreshTokenCommand command) {
        LoginHandler.TokenResult result = refreshTokenHandler.handle(command);
        return mapper.toAuthenticationResponse(result.accessToken(), result.refreshToken(), result.user());
    }
}