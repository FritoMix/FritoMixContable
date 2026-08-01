package com.fritomix.erp.modules.auth.application.dto.response;

import lombok.Builder;

@Builder
public record UserResponse(

        Long id,

        String firstName,

        String lastName,

        String email,

        String role

) {
}