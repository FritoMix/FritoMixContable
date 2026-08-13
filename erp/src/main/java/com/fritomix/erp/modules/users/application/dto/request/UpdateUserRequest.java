package com.fritomix.erp.modules.users.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateUserRequest(
        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName,

        @Email(message = "Formato de correo inválido")
        @Size(max = 150)
        String email,

        String password,

        String role,

        Boolean enabled
) {
}
