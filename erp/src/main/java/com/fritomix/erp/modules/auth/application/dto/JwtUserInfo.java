package com.fritomix.erp.modules.auth.application.dto;

public record JwtUserInfo(
        Long userId,
        String email,
        String role,
        String firstName,
        String lastName
) {
}