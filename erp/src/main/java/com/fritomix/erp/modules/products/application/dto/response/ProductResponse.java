package com.fritomix.erp.modules.products.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductResponse(
        Long id,
        String code,
        String name,
        String description,
        String unit,
        Boolean active,
        BigDecimal price,
        BigDecimal cost,
        Integer presentation,
        String weight,
        Integer weightGrams,
        Long categoryId,
        String categoryName,
        BigDecimal pesoUnidad,
        BigDecimal dimension,
        BigDecimal pesoTotalCargue,
        Integer stock,
        LocalDateTime createdAt
) {
}
