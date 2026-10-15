package com.fritomix.erp.modules.camaras.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CamaraVerificacionResponse(
        Long id,
        Long dispatchId,
        String dispatchNumber,
        String estadoCamaras,

        // Info del despacho
        String clienteNombre,
        String responsableDespacho,
        LocalDateTime fechaDespacho,
        String vehiclePlate,
        String driverName,
        String notesDespacho,
        String cumplimiento,

        // Info de verificación
        Long verificadoPorUserId,
        String verificadoPorNombre,
        LocalDateTime fechaInicioVerificacion,

        Long aprobadoPorUserId,
        String aprobadoPorNombre,
        LocalDateTime fechaAprobacion,

        String observacionesGenerales,

        // Detalle por línea
        List<DetalleVerificacionResponse> detalles,

        // Contadores rápidos para la bandeja
        int totalLineas,
        int lineasOk,
        int lineasConDiferencia,
        int lineasPendientes,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long version
) {

    @Builder
    public record DetalleVerificacionResponse(
            Long id,
            Long dispatchDetailId,
            Long productId,
            String productName,
            String productCode,
            String detalleProducto,
            String lote,
            BigDecimal cantidadDespachada,
            BigDecimal cantidadVerificada,
            BigDecimal diferencia,
            String estadoLinea,
            String tipoInconsistencia,
            String observacionLinea
    ) {}
}
