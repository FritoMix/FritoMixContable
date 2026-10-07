package com.fritomix.erp.modules.orders.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.orders.application.dto.request.OrderRequest;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.products.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDetailCalculatorTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderDetailCalculator calculator;

    private OrderRequest.OrderDetailRequest detail(long productId, String quantity) {
        return OrderRequest.OrderDetailRequest.builder()
                .productId(productId)
                .quantity(new BigDecimal(quantity))
                .build();
    }

    private void assertDecimal(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "esperado " + expected + " pero fue " + actual);
    }

    @Test
    void usaPesoUnidadCuandoEstaDefinido() {
        Product product = Product.builder().id(1L).pesoUnidad(new BigDecimal("2.5")).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Order order = Order.builder().build();
        calculator.applyDetails(order, List.of(detail(1L, "4")));

        assertDecimal("10", order.getPesoTotalCargue());
        assertDecimal("4", order.getTotal());
        assertEquals(1, order.getDetails().size());
        assertEquals(1L, order.getDetails().get(0).getProduct().getId());
    }

    @Test
    void calculaPesoDesdePresentacionYGramosCuandoNoHayPesoUnidad() {
        Product product = Product.builder()
                .id(2L)
                .presentation(10)
                .weightGrams(500)
                .build();
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        Order order = Order.builder().build();
        calculator.applyDetails(order, List.of(detail(2L, "2")));

        assertDecimal("10", order.getPesoTotalCargue());
    }

    @Test
    void pesoEsCeroCuandoNoHayDatosDePeso() {
        Product product = Product.builder()
                .id(3L)
                .pesoUnidad(BigDecimal.ZERO)
                .presentation(0)
                .weightGrams(0)
                .build();
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));

        Order order = Order.builder().build();
        calculator.applyDetails(order, List.of(detail(3L, "5")));

        assertDecimal("0", order.getPesoTotalCargue());
        assertDecimal("5", order.getTotal());
    }

    @Test
    void acumulaVariosDetalles() {
        Product a = Product.builder().id(1L).pesoUnidad(new BigDecimal("2.5")).build();
        Product b = Product.builder().id(2L).pesoUnidad(new BigDecimal("1.0")).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(a));
        when(productRepository.findById(2L)).thenReturn(Optional.of(b));

        Order order = Order.builder().build();
        calculator.applyDetails(order, List.of(detail(1L, "4"), detail(2L, "3")));

        assertDecimal("13.0", order.getPesoTotalCargue());
        assertDecimal("7", order.getTotal());
        assertEquals(2, order.getDetails().size());
    }

    @Test
    void reemplazaLosDetallesPrevios() {
        Product product = Product.builder().id(1L).pesoUnidad(BigDecimal.ONE).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Order order = Order.builder().build();
        calculator.applyDetails(order, List.of(detail(1L, "2")));
        calculator.applyDetails(order, List.of(detail(1L, "3")));

        assertEquals(1, order.getDetails().size());
        assertDecimal("3", order.getTotal());
    }

    @Test
    void lanzaExcepcionSiElProductoNoExiste() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Order order = Order.builder().build();

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> calculator.applyDetails(order, List.of(detail(99L, "1"))));
        assertEquals(true, ex.getMessage().contains("99"));
    }
}