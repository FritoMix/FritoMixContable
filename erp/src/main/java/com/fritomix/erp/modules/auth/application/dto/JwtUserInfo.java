package com.fritomix.erp.modules.auth.application.dto;

import java.util.List;

public record JwtUserInfo(
        Long userId,
        String email,
        String role,
        String firstName,
        String lastName,
        List<String> permissions
) {
}