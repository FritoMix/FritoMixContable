package com.fritomix.erp.modules.roles.application.mapper;

import com.fritomix.erp.modules.auth.domain.entity.Permission;
import com.fritomix.erp.modules.auth.domain.entity.Role;
import com.fritomix.erp.modules.roles.application.dto.response.RoleResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toList()))
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
