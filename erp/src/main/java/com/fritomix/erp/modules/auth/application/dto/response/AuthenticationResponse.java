package com.fritomix.erp.modules.auth.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String accessToken,
        @JsonIgnore String refreshToken,
        Long id,
        String firstName,
        String lastName,
        String email,
        String role
) {
}