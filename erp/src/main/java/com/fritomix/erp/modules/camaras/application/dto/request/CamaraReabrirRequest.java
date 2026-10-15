package com.fritomix.erp.modules.camaras.application.dto.request;

import jakarta.validation.constraints.Size;

public record CamaraReabrirRequest(
        @Size(max = 2000)
        String motivo
) {}
