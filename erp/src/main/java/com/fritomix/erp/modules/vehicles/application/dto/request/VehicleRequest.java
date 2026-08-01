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
        String plate,

        @NotBlank @Size(max = 100)
        String brand,

        @NotBlank @Size(max = 100)
        String model,

        @NotNull @PositiveOrZero
        BigDecimal capacity,

        Boolean active
) {
}
