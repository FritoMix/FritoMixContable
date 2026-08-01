package com.fritomix.erp.modules.auth.application.dto.response;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String accessToken,
        String refreshToken,
        Long id,
        String firstName,
        String lastName,
        String email,
        String role
) {
}