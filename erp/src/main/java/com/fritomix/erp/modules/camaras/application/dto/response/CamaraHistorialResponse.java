package com.fritomix.erp.modules.camaras.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CamaraHistorialResponse(
        Long id,
        String tipoEvento,
        Long userId,
        String nombreUsuario,
        String estadoAnterior,
        String estadoNuevo,
        String observacion,
        LocalDateTime createdAt
) {}
