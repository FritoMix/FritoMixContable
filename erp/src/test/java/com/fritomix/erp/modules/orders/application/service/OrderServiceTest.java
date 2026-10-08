package com.fritomix.erp.modules.orders.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.auth.application.dto.JwtUserInfo;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.customers.domain.entity.Customer;
import com.fritomix.erp.modules.customers.domain.entity.CustomerAddress;
import com.fritomix.erp.modules.customers.domain.repository.CustomerAddressRepository;
import com.fritomix.erp.modules.customers.domain.repository.CustomerRepository;
import com.fritomix.erp.modules.dispatch.domain.repository.DispatchRepository;
import com.fritomix.erp.modules.orders.application.dto.request.OrderRequest;
import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse;
import com.fritomix.erp.modules.orders.application.mapper.OrderMapper;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerAddressRepository addressRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderMapper mapper;
    @Mock
    private OrderDetailCalculator detailCalculator;
    @Mock
    private OrderNotifier orderNotifier;
    @Mock
    private DispatchRepository dispatchRepository;

    @InjectMocks
    private OrderService service;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private Customer customer(long id) {
        return Customer.builder().id(id).businessName("Cliente " + id).build();
    }

    private Order order(long id, String status) {
        return Order.builder()
                .id(id).customer(customer(id)).userId(1L)
                .orderNumber("ORD-" + id).status(status)
                .build();
    }

    private OrderResponse response(long id) {
        return OrderResponse.builder().id(id).orderNumber("ORD-" + id).build();
    }

    @Test
    void findAllMapeaConDireccionesUsuariosYDespachos() {
        Pageable pageable = PageRequest.of(0, 10);
        Order o = order(1L, "PENDIENTE");
        when(orderRepository.search(null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(o), pageable, 1));
        when(addressRepository.findAllMainByCustomerIds(List.of(1L))).thenReturn(List.of());
        when(userRepository.findAllById(List.of(1L))).thenReturn(List.<User>of());
        when(orderRepository.findDetailsByOrderIds(List.of(1L))).thenReturn(List.of());
        when(dispatchRepository.findAllByOrderIds(List.of(1L))).thenReturn(List.of());
        when(mapper.toResponse(any(Order.class), any(), any(), any(), any())).thenReturn(response(1L));

        PageResponse<OrderResponse> result = service.findAll(null, null, null, pageable);

        assertEquals(1, result.content().size());
        assertEquals(1L, result.content().get(0).id());
    }

    @Test
    void findAllConEstadoInvalidoLanzaExcepcion() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(IllegalArgumentException.class,
                () -> service.findAll(null, "NO_EXISTE", null, pageable));
    }

    @Test
    void findAllConStatusesInvalidosLanzaExcepcion() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(IllegalArgumentException.class,
                () -> service.findAll(null, null, List.of("APROBADO", "MALO"), pageable));
    }

    @Test
    void findByIdEncontradoYNoEncontrado() {
        Order o = order(1L, "PENDIENTE");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(o));
        when(mapper.toResponse(o)).thenReturn(response(1L));

        assertEquals(1L, service.findById(1L).id());

        when(orderRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }

    @Test
    void generateNextOrderNumberFormateaSecuencia() {
        when(orderRepository.maxOrderNumber()).thenReturn(10);

        assertEquals("PED-00011", service.generateNextOrderNumber());
    }

    @Test
    void createConNumeroAutomaticoYDetalles() {
        when(orderRepository.maxOrderNumber()).thenReturn(4);
        when(orderRepository.existsByOrderNumber("PED-00005")).thenReturn(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(Customer.builder().id(1L).build()));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Order.class))).thenReturn(response(1L));

        OrderRequest request = OrderRequest.builder()
                .customerId(1L).userId(2L)
                .details(List.of(OrderRequest.OrderDetailRequest.builder().productId(3L).quantity(BigDecimal.ONE).build()))
                .build();

        OrderResponse result = service.create(request);

        assertEquals(1L, result.id());
        verify(detailCalculator).applyDetails(any(Order.class), any());
        verify(orderNotifier).notifyCreated(any(Order.class), any(Customer.class), any());
    }

    @Test
    void createSinDetallesDejaCeros() {
        when(orderRepository.maxOrderNumber()).thenReturn(0);
        when(orderRepository.existsByOrderNumber("PED-00001")).thenReturn(false);
        Customer customer = Customer.builder().id(1L).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Order.class))).thenReturn(response(1L));

        service.create(OrderRequest.builder().customerId(1L).build());

        verify(detailCalculator, org.mockito.Mockito.never()).applyDetails(any(), any());
    }

    @Test
    void createConNumeroDuplicadoLanzaExcepcion() {
        when(orderRepository.existsByOrderNumber("ORD-1")).thenReturn(true);

        OrderRequest request = OrderRequest.builder().customerId(1L).orderNumber("ORD-1").build();

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
    }

    @Test
    void createClienteInexistenteLanzaExcepcion() {
        when(orderRepository.existsByOrderNumber("ORD-1")).thenReturn(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        OrderRequest request = OrderRequest.builder().customerId(1L).orderNumber("ORD-1").build();

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void updateExiticoCambiaClienteYNumero() {
        Order o = order(1L, "PENDIENTE");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(o));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(Customer.builder().id(2L).build()));
        when(orderRepository.existsByOrderNumber("NUEVO")).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Order.class))).thenReturn(response(1L));

        service.update(1L, OrderRequest.builder().customerId(2L).orderNumber("NUEVO").userId(5L).notes("n").build());

        assertEquals("NUEVO", o.getOrderNumber());
        assertEquals(5L, o.getUserId());
        assertEquals("n", o.getNotes());
    }

    @Test
    void updateSoloPermitePendientes() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order(1L, "APROBADO")));

        OrderRequest request = OrderRequest.builder().customerId(1L).build();

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, request));
    }

    @Test
    void updateConNumeroDuplicadoLanzaExcepcion() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order(1L, "PENDIENTE")));
        when(orderRepository.existsByOrderNumber("DUP")).thenReturn(true);

        OrderRequest request = OrderRequest.builder().orderNumber("DUP").build();

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, request));
    }

    @Test
    void updateConCambioDeEstadoLanzaExcepcion() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order(1L, "PENDIENTE")));

        OrderRequest request = OrderRequest.builder().status("APROBADO").build();

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, request));
    }

    @Test
    void deleteNoPermitePedidosCerrados() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order(1L, "APROBADO")));

        assertThrows(IllegalArgumentException.class, () -> service.delete(1L));
    }

    @Test
    void deleteExitoso() {
        Order o = order(1L, "PENDIENTE");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(o));

        service.delete(1L);

        verify(orderRepository).delete(o);
    }

    @Test
    void deleteNoEncontradoLanzaExcepcion() {
        when(orderRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(9L));
    }

    @Test
    void updateStatusAAprobadoRegistraAprobador() {
        Order o = order(1L, "PENDIENTE");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(o));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Order.class))).thenReturn(response(1L));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new JwtUserInfo(7L, "u@x.com", "ADMIN", "Ana", "Soto", List.of()), null, List.of()));

        service.updateStatus(1L, "aprobado");

        assertEquals("APROBADO", o.getStatus());
        assertEquals(7L, o.getApprovedById());
        assertTrue(o.getApprovedAt() != null);
        verify(orderNotifier).notifyStatusChanged(o, "APROBADO");
    }

    @Test
    void updateStatusInvalidoLanzaExcepcion() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order(1L, "PENDIENTE")));

        assertThrows(IllegalArgumentException.class, () -> service.updateStatus(1L, "MALO"));
    }

    @Test
    void updateStatusTransicionInvalidaLanzaExcepcion() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order(1L, "PENDIENTE")));

        assertThrows(IllegalArgumentException.class, () -> service.updateStatus(1L, "EN_PRODUCCION"));
    }

    @Test
    void updateProductionStatusExitoso() {
        Order o = order(1L, "APROBADO");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(o));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Order.class))).thenReturn(response(1L));

        service.updateProductionStatus(1L, "en_produccion");

        assertEquals("EN_PRODUCCION", o.getStatus());
    }

    @Test
    void updateProductionStatusInvalidoLanzaExcepcion() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order(1L, "APROBADO")));

        assertThrows(IllegalArgumentException.class, () -> service.updateProductionStatus(1L, "APROBADO"));
    }

    @Test
    void updateTipoPedidoValidaEntradas() {
        assertThrows(IllegalArgumentException.class, () -> service.updateTipoPedido(List.of(), "pedido_unico"));
        assertThrows(IllegalArgumentException.class, () -> service.updateTipoPedido(List.of(1L), "otro"));
    }

    @Test
    void updateTipoPedidoNoEncontradoLanzaExcepcion() {
        when(orderRepository.findAllById(List.of(1L))).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateTipoPedido(List.of(1L), "pedido_unico"));
    }

    @Test
    void updateTipoPedidoExitoso() {
        Order o = order(1L, "PENDIENTE");
        when(orderRepository.findAllById(List.of(1L))).thenReturn(List.of(o));
        when(orderRepository.saveAll(List.of(o))).thenReturn(List.of(o));
        when(mapper.toResponse(o)).thenReturn(response(1L));

        List<OrderResponse> result = service.updateTipoPedido(List.of(1L), "Pedido_Multipedido");

        assertEquals(1, result.size());
        assertEquals("pedido_multipedido", o.getTipoPedido());
    }
}