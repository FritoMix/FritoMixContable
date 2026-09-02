package com.fritomix.erp.modules.push.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PushSubscriptionRequest(
        @NotBlank(message = "El endpoint es obligatorio") String endpoint,
        @NotBlank(message = "La clave p256dh es obligatoria") String p256dh,
        @NotBlank(message = "La clave auth es obligatoria") String auth
) {
}
