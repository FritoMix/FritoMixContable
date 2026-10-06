package com.fritomix.erp.modules.orders.application.service;

import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse;
import com.fritomix.erp.modules.orders.application.mapper.OrderMapper;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Consultas de pedidos desde la perspectiva de despacho.
 *
 * <p>Vive en el módulo {@code orders} porque "qué pedidos están listos para cargarse"
 * es una consulta sobre el agregado pedido, no sobre el agregado despacho. El endpoint
 * que la expone ({@code GET /api/v1/dispatches/listos-cargue}) se compone en el
 * controlador, de modo que dispatch no necesita conocer el repositorio ni el mapper
 * de pedidos para resolver una lectura.
 */
@Service
@RequiredArgsConstructor
public class OrderDispatchQueryService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public List<OrderResponse> findReadyForDispatch() {
        return orderRepository.findReadyForDispatch().stream()
                .map(orderMapper::toResponse)
                .toList();
    }
}