package com.fritomix.erp.modules.auth.application.mapper;

import com.fritomix.erp.modules.auth.application.dto.response.AuthenticationResponse;
import com.fritomix.erp.modules.auth.application.dto.response.UserResponse;
import com.fritomix.erp.modules.auth.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationMapper {

    public AuthenticationResponse toAuthenticationResponse(String accessToken, String refreshToken, User user) {
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .build();
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .build();
    }
}
