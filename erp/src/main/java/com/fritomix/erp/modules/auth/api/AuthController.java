package com.fritomix.erp.modules.auth.api;

import com.fritomix.erp.modules.auth.application.command.LoginCommand;
import com.fritomix.erp.modules.auth.application.command.RefreshTokenCommand;
import com.fritomix.erp.modules.auth.application.dto.request.LoginRequest;
import com.fritomix.erp.modules.auth.application.dto.request.RefreshTokenRequest;
import com.fritomix.erp.modules.auth.application.dto.response.AuthenticationResponse;
import com.fritomix.erp.modules.auth.application.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = LoginCommand.builder()
                .email(request.email())
                .password(request.password())
                .build();
        AuthenticationResponse response = authenticationService.login(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenCommand command = RefreshTokenCommand.builder()
                .refreshToken(request.refreshToken())
                .build();
        AuthenticationResponse response = authenticationService.refresh(command);
        return ResponseEntity.ok(response);
    }
}
