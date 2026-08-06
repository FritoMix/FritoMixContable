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
        String vehiclePlate,
        String vehicleBrand,
        String vehicleModel,
        LocalDateTime dispatchDate,
        String status,
        String cumplimiento,
        String notes,
        String dispatchUserName,
        List<DispatchDetailResponse> details,
        LocalDateTime createdAt
) {

    @Builder
    public record OrderInfo(
            Long id,
            String orderNumber,
            String clientName,
            BigDecimal pesoTotalCargue
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
}
