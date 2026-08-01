package com.fritomix.erp.modules.orders.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderRequest(
        @NotNull
        Long customerId,

        @NotNull
        Long userId,

        @NotBlank @Size(max = 50)
        String orderNumber,

        LocalDateTime orderDate,

        @Size(max = 30)
        String status,

        @NotNull @PositiveOrZero
        BigDecimal total,

        String notes,

        @Valid
        List<OrderDetailRequest> details
) {

    @Builder
    public record OrderDetailRequest(
            @NotNull
            Long productId,

            @NotNull @PositiveOrZero
            BigDecimal quantity
    ) {}
}
