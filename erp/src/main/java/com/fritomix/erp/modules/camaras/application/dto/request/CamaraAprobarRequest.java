package com.fritomix.erp.modules.camaras.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CamaraAprobarRequest(
        @NotBlank(message = "La confirmación de aprobación es obligatoria")
        @Size(max = 2000)
        String confirmacion
) {}
