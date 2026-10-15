package com.fritomix.erp.modules.camaras.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CamaraObservarRequest(
        @NotBlank(message = "La observación es obligatoria")
        @Size(max = 2000)
        String observacion
) {}
