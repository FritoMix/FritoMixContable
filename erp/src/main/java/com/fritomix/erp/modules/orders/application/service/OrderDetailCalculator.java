package com.fritomix.erp.modules.orders.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.orders.application.dto.request.OrderRequest;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.entity.OrderDetail;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.products.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Construye los detalles de un pedido y calcula sus totales.
 */
@Component
@RequiredArgsConstructor
public class OrderDetailCalculator {

    private final ProductRepository productRepository;

    public void applyDetails(Order order, List<OrderRequest.OrderDetailRequest> details) {
        BigDecimal pesoTotal = BigDecimal.ZERO;
        BigDecimal totalBultos = BigDecimal.ZERO;

        order.getDetails().clear();
        for (OrderRequest.OrderDetailRequest dto : details) {
            Product product = productRepository.findById(dto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + dto.productId()));

            OrderDetail detail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(dto.quantity())
                    .build();
            order.getDetails().add(detail);
            totalBultos = totalBultos.add(dto.quantity());

            if (product.getPesoTotalCargue() != null) {
                pesoTotal = pesoTotal.add(product.getPesoTotalCargue().multiply(dto.quantity()));
            }
        }
        order.setPesoTotalCargue(pesoTotal);
        order.setTotal(totalBultos);
    }
}