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

            BigDecimal unitWeight = BigDecimal.ZERO;
            if (product.getPesoUnidad() != null && product.getPesoUnidad().compareTo(BigDecimal.ZERO) > 0) {
                unitWeight = product.getPesoUnidad();
            } else if (product.getPresentation() != null && product.getPresentation() > 0 
                    && product.getWeightGrams() != null && product.getWeightGrams() > 0) {
                unitWeight = BigDecimal.valueOf((long) product.getPresentation() * product.getWeightGrams())
                        .divide(BigDecimal.valueOf(1000), 4, java.math.RoundingMode.HALF_UP);
            }
            pesoTotal = pesoTotal.add(unitWeight.multiply(dto.quantity()));
        }
        order.setPesoTotalCargue(pesoTotal);
        order.setTotal(totalBultos);
    }
}