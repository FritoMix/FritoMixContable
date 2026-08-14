package com.fritomix.erp.modules.dispatch.application.service;

import com.fritomix.erp.exception.PedidoYaDespachadoException;
import com.fritomix.erp.modules.dispatch.application.dto.request.DispatchRequest;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse;
import com.fritomix.erp.modules.dispatch.application.mapper.DispatchMapper;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.dispatch.domain.repository.DispatchRepository;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.drivers.domain.repository.DriverRepository;
import com.fritomix.erp.modules.notifications.application.service.EmailService;
import com.fritomix.erp.modules.notifications.application.service.NotificationService;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import com.fritomix.erp.modules.products.domain.entity.Category;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.products.domain.repository.ProductRepository;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import com.fritomix.erp.modules.vehicles.domain.repository.VehicleRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock
    private DispatchRepository dispatchRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DispatchMapper mapper;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private DispatchService dispatchService;

    private Order order;
    private Driver driver;
    private Vehicle vehicle;
    private Product product;
    private Dispatch dispatch;
    private DispatchRequest validRequest;

    @BeforeEach
    void setUp() {
        Category category = Category.builder().id(1L).name("Test").build();
        product = Product.builder().id(1L).code("PROD-001").name("Test Product").category(category).unit("CAJA").build();

        order = Order.builder().id(1L).orderNumber("ORD-001").build();
        driver = Driver.builder().id(1L).name("Test Driver").build();
        vehicle = Vehicle.builder().id(1L).vehicleNumber("VEH-001").build();

        DispatchDetail detail = DispatchDetail.builder()
                .id(1L)
                .product(product)
                .quantity(new BigDecimal("10"))
                .delivered(BigDecimal.ZERO)
                .build();

        dispatch = Dispatch.builder()
                .id(1L)
                .orders(List.of(order))
                .driver(driver)
                .vehicle(vehicle)
                .dispatchNumber("DES-001")
                .status("LISTO_CARGUE")
                .details(List.of(detail))
                .build();

        validRequest = DispatchRequest.builder()
                .orderId(1L)
                .driverId(1L)
                .vehicleId(1L)
                .dispatchNumber("DES-001")
                .status("PENDIENTE")
                .details(List.of(
                        DispatchRequest.DispatchDetailRequest.builder()
                                .productId(1L)
                                .quantity(new BigDecimal("10"))
                                .build()
                ))
                .build();
    }

    @Test
    void create_shouldSucceed() {
        when(dispatchRepository.existsByDispatchNumber("DES-001")).thenReturn(false);
        when(orderRepository.findAllById(List.of(1L))).thenReturn(List.of(order));
        when(dispatchRepository.findAllByOrderId(eq(1L))).thenReturn(List.of());
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(dispatchRepository.save(any(Dispatch.class))).thenReturn(dispatch);
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(
                DispatchResponse.builder().id(1L).dispatchNumber("DES-001").build());

        DispatchResponse response = dispatchService.create(validRequest);

        assertNotNull(response);
        assertEquals("DES-001", response.dispatchNumber());
    }

    @Test
    void create_shouldThrowWhenDuplicateDispatchNumber() {
        when(dispatchRepository.existsByDispatchNumber("DES-001")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> dispatchService.create(validRequest));
        verify(orderRepository, never()).findAllById(any());
    }

    @Test
    void create_shouldThrowWhenOrderAlreadyHasActiveDispatch() {
        when(dispatchRepository.existsByDispatchNumber("DES-001")).thenReturn(false);
        when(orderRepository.findAllById(List.of(1L))).thenReturn(List.of(order));
        when(dispatchRepository.findAllByOrderId(eq(1L))).thenReturn(List.of(dispatch));

        assertThrows(PedidoYaDespachadoException.class, () -> dispatchService.create(validRequest));
        verify(driverRepository, never()).findById(any());
    }

    @Test
    void updateStatus_shouldSucceed() {
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(dispatch));
        when(dispatchRepository.save(any(Dispatch.class))).thenReturn(dispatch);
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(
                DispatchResponse.builder().id(1L).dispatchNumber("DES-001").status("DESPACHADO").build());

        DispatchResponse response = dispatchService.updateStatus(1L, "DESPACHADO");

        assertNotNull(response);
    }

    @Test
    void delete_shouldSucceed() {
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(dispatch));

        dispatchService.delete(1L);

        verify(dispatchRepository).delete(dispatch);
    }

    @Test
    void delete_shouldThrowWhenDispatchDespachado() {
        dispatch.setStatus("DESPACHADO");
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(dispatch));

        assertThrows(IllegalArgumentException.class, () -> dispatchService.delete(1L));
        verify(dispatchRepository, never()).delete(any());
    }

    @Test
    void update_shouldThrowWhenDispatchDespachado() {
        dispatch.setStatus("DESPACHADO");
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(dispatch));

        assertThrows(IllegalArgumentException.class, () -> dispatchService.update(1L, validRequest));
        verify(dispatchRepository, never()).save(any());
    }

    @Test
    void updateStatus_shouldThrowOnBackwardTransition() {
        dispatch.setStatus("PRODUCCION");
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(dispatch));

        assertThrows(IllegalArgumentException.class, () -> dispatchService.updateStatus(1L, "PENDIENTE"));
        verify(dispatchRepository, never()).save(any());
    }
}
