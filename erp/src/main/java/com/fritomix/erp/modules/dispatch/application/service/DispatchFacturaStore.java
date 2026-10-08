package com.fritomix.erp.modules.dispatch.application.service;

import com.fritomix.erp.modules.dispatch.application.dto.request.DispatchRequest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistencia del numero de factura por pedido dentro de un despacho.
 *
 * La tabla de enlace {@code dispatch_orders} se actualiza y consulta con SQL nativo,
 * por lo que vive fuera del modelo JPA de {@code Dispatch}. Concentrarla aqui mantiene
 * el {@code EntityManager} fuera del servicio de dominio.
 */
@Component
@RequiredArgsConstructor
public class DispatchFacturaStore {

    private final EntityManager em;

    public void saveFacturas(Long dispatchId, List<DispatchRequest.OrderFacturaRequest> orderFacturas) {
        if (orderFacturas == null || orderFacturas.isEmpty()) return;
        for (DispatchRequest.OrderFacturaRequest of : orderFacturas) {
            if (of.orderId() != null) {
                em.createNativeQuery("UPDATE dispatch_orders SET numero_factura = :numeroFactura WHERE dispatch_id = :dispatchId AND order_id = :orderId")
                        .setParameter("numeroFactura", of.numeroFactura())
                        .setParameter("dispatchId", dispatchId)
                        .setParameter("orderId", of.orderId())
                        .executeUpdate();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Map<Long, String> loadFacturas(Long dispatchId) {
        Map<Long, String> result = new HashMap<>();
        if (dispatchId == null) return result;
        List<Object[]> rows = em.createNativeQuery("SELECT order_id, numero_factura FROM dispatch_orders WHERE dispatch_id = :dispatchId")
                .setParameter("dispatchId", dispatchId)
                .getResultList();
        for (Object[] row : rows) {
            if (row[0] != null) {
                Long orderId = ((Number) row[0]).longValue();
                String factura = (String) row[1];
                result.put(orderId, factura);
            }
        }
        return result;
    }
}
