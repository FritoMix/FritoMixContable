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
        @Size(max = 20)
        String tipoPedido,

        Long orderId,

        List<Long> orderIds,

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

        @Size(max = 50)
        String numeroFactura,

        List<OrderFacturaRequest> orderFacturas,

        @Valid
        List<DispatchDetailRequest> details,

        @Valid
        List<ArrumeRequest> arrumes
) {


    @Builder
    public record OrderFacturaRequest(
            Long orderId,
            String numeroFactura
    ) {}


    @Builder
    public record DispatchDetailRequest(
            @NotNull
            Long productId,

            @NotNull
            BigDecimal quantity,

            BigDecimal delivered,

            String observations,

            String detalleProducto,

            String lote
    ) {}

    @Builder
    public record ArrumeRequest(
            Integer numArrume,

            String arrumeProducto,

            BigDecimal cantidad,

            String lote
    ) {}
}
