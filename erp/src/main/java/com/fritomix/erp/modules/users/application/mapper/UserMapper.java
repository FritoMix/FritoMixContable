package com.fritomix.erp.modules.users.application.mapper;

import com.fritomix.erp.modules.auth.domain.entity.Role;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.users.application.dto.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .enabled(user.getEnabled())
                .accountNonLocked(user.getAccountNonLocked())
                .failedAttempts(user.getFailedAttempts())
                .accountNonExpired(user.getAccountNonExpired())
                .credentialsNonExpired(user.getCredentialsNonExpired())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toEntity(com.fritomix.erp.modules.users.application.dto.request.CreateUserRequest request, Role role, String encodedPassword) {
        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(encodedPassword)
                .role(role)
                .enabled(true)
                .accountNonLocked(true)
                .failedAttempts(0)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .build();
    }
}
