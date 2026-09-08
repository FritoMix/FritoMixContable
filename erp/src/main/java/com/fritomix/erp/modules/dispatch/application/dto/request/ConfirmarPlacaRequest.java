package com.fritomix.erp.modules.dispatch.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConfirmarPlacaRequest(
        @NotNull(message = "Debe seleccionar el despachador") Long despachadorId,
        @NotBlank(message = "Debe confirmar la placa del vehículo") String placa,
        String observacionPlaca
) {
}