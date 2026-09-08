package com.fritomix.erp.modules.dispatch.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderPlacaRequest(
        @NotNull(message = "El pedido es obligatorio") Long orderId,
        @NotBlank(message = "Debe ingresar la placa del vehículo") String placa
) {}