package com.fritomix.erp.modules.orders.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateOrderTipoPedidoRequest(
        @NotEmpty(message = "Debes proporcionar al menos un ID de pedido")
        List<Long> orderIds,

        @NotNull(message = "El tipo de pedido es requerido")
        String tipoPedido
) {}
