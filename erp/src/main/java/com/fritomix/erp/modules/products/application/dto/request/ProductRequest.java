package com.fritomix.erp.modules.products.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductRequest(
        @NotNull Long categoryId,

        @NotBlank @Size(max = 50)
        String code,

        @NotBlank @Size(max = 150)
        String name,

        @Size(max = 250)
        String description,

        @NotBlank @Size(max = 30)
        String unit,

        Integer presentation,
        String weight,
        Integer weightGrams,
        Boolean active,
        BigDecimal pesoUnidad,
        BigDecimal dimension,
        BigDecimal pesoTotalCargue
) {
}
