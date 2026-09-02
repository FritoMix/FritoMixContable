package com.fritomix.erp.modules.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VerifyResetCodeRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Correo inválido")
        String email,

        @NotBlank(message = "El código es obligatorio")
        @Size(min = 6, max = 6, message = "El código debe tener 6 dígitos")
        @Pattern(regexp = "\\d{6}", message = "El código debe ser numérico de 6 dígitos")
        String code
) {
}
