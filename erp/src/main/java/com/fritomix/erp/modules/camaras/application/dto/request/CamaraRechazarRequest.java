package com.fritomix.erp.modules.camaras.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CamaraRechazarRequest(
        @NotBlank(message = "El motivo de rechazo es obligatorio")
        @Size(max = 2000)
        String motivo
) {}
