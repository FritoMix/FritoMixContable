package com.fritomix.erp.modules.dispatch.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DispatchRequest(
        @NotNull
        Long orderId,

        @NotNull
        Long driverId,

        @NotNull
        Long vehicleId,

        Long userId,

        @NotBlank @Size(max = 50)
        String dispatchNumber,

        LocalDateTime dispatchDate,

        @Size(max = 30)
        String status,

        String notes,

        @Valid
        List<DispatchDetailRequest> details
) {

    @Builder
    public record DispatchDetailRequest(
            @NotNull
            Long productId,

            @NotNull
            BigDecimal quantity,

            BigDecimal delivered,

            String observations
    ) {}
}
