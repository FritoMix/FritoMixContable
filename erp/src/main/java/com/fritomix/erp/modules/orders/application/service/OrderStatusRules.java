package com.fritomix.erp.modules.orders.application.service;

import java.util.Set;

/**
 * Reglas puras de estados y transiciones de un pedido.
 */
public final class OrderStatusRules {

    public static final Set<String> ALLOWED_ORDER_STATUS = Set.of("PENDIENTE", "APROBADO", "CANCELADO", "EN_PRODUCCION", "LISTO_PRODUCCION");
    public static final Set<String> CLOSED_ORDER_STATUS = Set.of("APROBADO", "CANCELADO", "LISTO_PRODUCCION");
    public static final Set<String> PRODUCTION_ORDER_STATUS = Set.of("EN_PRODUCCION", "LISTO_PRODUCCION");
    public static final String STATUS_PENDIENTE = "PENDIENTE";
    public static final String STATUS_APROBADO = "APROBADO";
    public static final String STATUS_CANCELADO = "CANCELADO";
    public static final String STATUS_EN_PRODUCCION = "EN_PRODUCCION";
    public static final String STATUS_LISTO_PRODUCCION = "LISTO_PRODUCCION";

    private OrderStatusRules() {
    }

    public static boolean isValidStatus(String status) {
        return status != null && ALLOWED_ORDER_STATUS.contains(status);
    }

    public static boolean isClosed(String status) {
        return CLOSED_ORDER_STATUS.contains(status);
    }

    public static boolean isProductionStatus(String status) {
        return status != null && PRODUCTION_ORDER_STATUS.contains(status);
    }

    public static boolean canTransition(String current, String next) {
        if (current.equals(next)) {
            return true;
        }
        return switch (current) {
            case STATUS_PENDIENTE -> next.equals(STATUS_APROBADO) || next.equals(STATUS_CANCELADO);
            case STATUS_APROBADO -> next.equals(STATUS_CANCELADO) || next.equals(STATUS_EN_PRODUCCION);
            case STATUS_EN_PRODUCCION -> next.equals(STATUS_LISTO_PRODUCCION) || next.equals(STATUS_CANCELADO);
            default -> false;
        };
    }
}