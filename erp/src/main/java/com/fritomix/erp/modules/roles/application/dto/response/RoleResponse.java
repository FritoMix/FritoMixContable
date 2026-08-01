package com.fritomix.erp.modules.roles.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RoleResponse(
        Long id,
        String name,
        String description,
        List<String> permissions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
