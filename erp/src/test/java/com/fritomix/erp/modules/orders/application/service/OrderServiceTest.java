package com.fritomix.erp.modules.orders.application.service;

import com.fritomix.erp.modules.customers.domain.entity.Customer;
import com.fritomix.erp.modules.customers.domain.repository.CustomerRepository;
import com.fritomix.erp.modules.notifications.application.service.EmailService;
import com.fritomix.erp.modules.notifications.application.service.NotificationService;
import com.fritomix.erp.modules.orders.application.dto.request.OrderRequest;
import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse;
import com.fritomix.erp.modules.orders.application.mapper.OrderMapper;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import com.fritomix.erp.modules.products.domain.entity.Category;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.products.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper mapper;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private OrderNotifier orderNotifier;

    @Mock
    private OrderDetailCalculator detailCalculator;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Product product;
    private OrderRequest validRequest;
    private Order order;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .businessName("Test Client")
                .document("123456789")
                .build();

        Category category = Category.builder().id(1L).name("Test Category").build();
        product = Product.builder()
                .id(1L)
                .code("PROD-001")
                .name("Test Product")
                .category(category)
                .unit("CAJA")
                .build();

        validRequest = OrderRequest.builder()
                .customerId(1L)
                .userId(1L)
                .orderNumber("ORD-001")
                .details(List.of(
                        OrderRequest.OrderDetailRequest.builder()
                                .productId(1L)
                                .quantity(new BigDecimal("10"))
                                .build()
                ))
                .build();

        order = Order.builder()
                .id(1L)
                .customer(customer)
                .userId(1L)
                .orderNumber("ORD-001")
                .total(new BigDecimal("10"))
                .build();
    }

    @Test
    void create_shouldSucceed() {
        when(orderRepository.existsByOrderNumber("ORD-001")).thenReturn(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(mapper.toResponse(any(Order.class))).thenReturn(
                OrderResponse.builder().id(1L).orderNumber("ORD-001").build());

        OrderResponse response = orderService.create(validRequest);

        assertNotNull(response);
        assertEquals("ORD-001", response.orderNumber());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void create_shouldThrowWhenDuplicateOrderNumber() {
        when(orderRepository.existsByOrderNumber("ORD-001")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> orderService.create(validRequest));
        verify(customerRepository, never()).findById(any());
    }

    @Test
    void update_shouldThrowWhenOrderNotPending() {
        order.setStatus("APROBADO");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.update(1L, validRequest));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateStatus_shouldThrowOnBackwardTransition() {
        order.setStatus("APROBADO");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(1L, "PENDIENTE"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateStatus_shouldAllowApprovalFromPending() {
        order.setStatus("PENDIENTE");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(mapper.toResponse(any(Order.class))).thenReturn(
                OrderResponse.builder().id(1L).orderNumber("ORD-001").status("APROBADO").build());

        OrderResponse response = orderService.updateStatus(1L, "APROBADO");

        assertNotNull(response);
        assertEquals("APROBADO", response.status());
        verify(orderRepository).save(order);
    }

    @Test
    void updateProductionStatus_shouldAllowStartFromApproved() {
        order.setStatus("APROBADO");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(mapper.toResponse(any(Order.class))).thenReturn(
                OrderResponse.builder().id(1L).orderNumber("ORD-001").status("EN_PRODUCCION").build());

        OrderResponse response = orderService.updateProductionStatus(1L, "EN_PRODUCCION");

        assertNotNull(response);
        assertEquals("EN_PRODUCCION", response.status());
        assertEquals("EN_PRODUCCION", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateProductionStatus_shouldRejectSkipToReady() {
        order.setStatus("APROBADO");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.updateProductionStatus(1L, "LISTO_PRODUCCION"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateProductionStatus_shouldRejectNonProductionStatus() {
        order.setStatus("APROBADO");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.updateProductionStatus(1L, "APROBADO"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void delete_shouldThrowWhenOrderClosed() {
        order.setStatus("CANCELADO");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.delete(1L));
        verify(orderRepository, never()).delete(any());
    }
}
