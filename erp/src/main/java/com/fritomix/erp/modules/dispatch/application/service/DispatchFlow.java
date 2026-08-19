package com.fritomix.erp.modules.dispatch.application.service;

import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Reglas puras del flujo de estados y cumplimiento de un despacho.
 */
public final class DispatchFlow {

    public static final Set<String> VALID_TIPO_PEDIDO = Set.of("pedido_unico", "pedido_multipedido");
    public static final Set<String> VALID_STATUS = Set.of(
            "PENDIENTE", "ELABORACION", "PRODUCCION", "LISTO_CARGUE", "DESPACHADO");
    public static final List<String> STATUS_FLOW = List.of(
            "PENDIENTE", "ELABORACION", "PRODUCCION", "LISTO_CARGUE", "DESPACHADO");
    public static final Set<String> STATUS_CERRADOS = Set.of("DESPACHADO");
    public static final String CUMPLIMIENTO_COMPLETO = "COMPLETO";
    public static final String CUMPLIMIENTO_PARCIAL = "PARCIAL";

    private DispatchFlow() {
    }

    public static boolean isValidTipoPedido(String tipoPedido) {
        return VALID_TIPO_PEDIDO.contains(tipoPedido);
    }

    public static boolean isValidStatus(String status) {
        return status != null && VALID_STATUS.contains(status);
    }

    public static boolean isClosed(String status) {
        return STATUS_CERRADOS.contains(status);
    }

    /**
     * Valida el estado inicial (PENDIENTE o ELABORACION) y devuelve el estado
     * normalizado a mayúsculas o PENDIENTE por defecto.
     */
    public static String initialStatus(String status) {
        String normalized = status != null ? status.toUpperCase() : "PENDIENTE";
        if (!STATUS_FLOW.subList(0, 2).contains(normalized)) {
            throw new IllegalArgumentException("Estado inicial inválido para el despacho: " + status);
        }
        return normalized;
    }

    /**
     * Valida que una transición no retroceda en el flujo.
     */
    public static void validateTransition(String current, String next) {
        int currentIdx = STATUS_FLOW.indexOf(current);
        int newIdx = STATUS_FLOW.indexOf(next);
        if (newIdx < 0 || newIdx < currentIdx) {
            throw new IllegalArgumentException("Estado inválido o retroceso de flujo para el despacho: " + next);
        }
    }

    public static String calcularCumplimiento(List<DispatchDetail> details) {
        if (details == null || details.isEmpty()) {
            return CUMPLIMIENTO_PARCIAL;
        }
        boolean parcial = false;
        for (DispatchDetail detail : details) {
            BigDecimal solicitado = detail.getQuantity() != null ? detail.getQuantity() : BigDecimal.ZERO;
            BigDecimal despachado = detail.getDelivered() != null ? detail.getDelivered() : BigDecimal.ZERO;
            if (despachado.compareTo(solicitado) < 0) {
                parcial = true;
                break;
            }
        }
        return parcial ? CUMPLIMIENTO_PARCIAL : CUMPLIMIENTO_COMPLETO;
    }
}