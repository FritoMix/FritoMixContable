package com.fritomix.erp.modules.vehicles.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record VehicleRequest(
        @NotBlank @Size(max = 20)
        String vehicleNumber,

        @NotBlank @Size(max = 100)
        String type,

        @NotNull @PositiveOrZero
        BigDecimal capacity,

        @NotNull @PositiveOrZero
        BigDecimal dimension,

        Boolean active
) {
}
