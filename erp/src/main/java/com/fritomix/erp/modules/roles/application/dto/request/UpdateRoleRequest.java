package com.fritomix.erp.modules.roles.application.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateRoleRequest(
        @Size(max = 50)
        String name,

        @Size(max = 150)
        String description,

        List<String> permissions
) {
}
