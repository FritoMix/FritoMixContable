package com.fritomix.erp.modules.multipedidos.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMultipedidoRequest {
    @NotEmpty(message = "Debe incluir al menos un pedido")
    private List<Long> orderIds;
}
