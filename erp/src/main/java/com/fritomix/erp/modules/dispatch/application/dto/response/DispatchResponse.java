package com.fritomix.erp.modules.dispatch.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DispatchResponse(
        Long id,
        String dispatchNumber,
        String tipoPedido,
        List<OrderInfo> orders,
        Long orderId,
        String orderNumber,
        BigDecimal pesoTotal,
        BigDecimal totalDimension,
        BigDecimal pesoTotalCargue,
        Long driverId,
        String driverName,
        String driverDocument,
        Long vehicleId,
        String vehicleNumber,
        String vehicleType,
        LocalDateTime dispatchDate,
        String status,
        String cumplimiento,
        String notes,
        String numeroFactura,
        String dispatchUserName,
        List<DispatchDetailResponse> details,
        List<ArrumeResponse> arrumes,
        LocalDateTime createdAt
) {

    @Builder
    public record OrderInfo(
            Long id,
            String orderNumber,
            String clientName,
            BigDecimal pesoTotalCargue,
            String numeroFactura
    ) {}


    @Builder
    public record DispatchDetailResponse(
            Long id,
            Long productId,
            String productName,
            String productCode,
            BigDecimal quantity,
            BigDecimal delivered,
            String observations,
            String detalleProducto,
            String lote
    ) {}

    @Builder
    public record ArrumeResponse(
            Long id,
            Integer numArrume,
            String arrumeProducto,
            BigDecimal cantidad,
            String lote
    ) {}
}
