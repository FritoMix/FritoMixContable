package com.fritomix.erp.modules.multipedidos.application.dto.response;

import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MultipedidoResponse(
        Long id,
        String numero,
        String status,
        BigDecimal totalPeso,
        BigDecimal totalMonto,
        List<OrderResponse> orders,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
