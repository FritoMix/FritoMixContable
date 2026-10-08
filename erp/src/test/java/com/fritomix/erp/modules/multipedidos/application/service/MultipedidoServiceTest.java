package com.fritomix.erp.modules.multipedidos.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.multipedidos.application.dto.request.CreateMultipedidoRequest;
import com.fritomix.erp.modules.multipedidos.application.dto.response.MultipedidoResponse;
import com.fritomix.erp.modules.multipedidos.domain.entity.Multipedido;
import com.fritomix.erp.modules.multipedidos.domain.repository.MultipedidoRepository;
import com.fritomix.erp.modules.orders.application.mapper.OrderMapper;
import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MultipedidoServiceTest {

    @Mock
    private MultipedidoRepository multipedidoRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private MultipedidoService service;

    private Order order(long id, BigDecimal peso, BigDecimal total) {
        return Order.builder()
                .id(id)
                .pesoTotalCargue(peso)
                .total(total)
                .build();
    }

    @Test
    void createMultipedido_sinPedidosLanzaExcepcion() {
        CreateMultipedidoRequest req = CreateMultipedidoRequest.builder().orderIds(new ArrayList<>()).build();

        assertThrows(IllegalArgumentException.class, () -> service.createMultipedido(req));
    }

    @Test
    void createMultipedido_conPedidosNoEncontradosLanzaExcepcion() {
        CreateMultipedidoRequest req = CreateMultipedidoRequest.builder().orderIds(List.of(1L, 2L)).build();
        when(orderRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(order(1L, BigDecimal.ONE, BigDecimal.ONE)));

        assertThrows(ResourceNotFoundException.class, () -> service.createMultipedido(req));
    }

    @Test
    void createMultipedido_exitoso_conNumeracionInicial() {
        CreateMultipedidoRequest req = CreateMultipedidoRequest.builder().orderIds(List.of(1L)).build();
        Order o = order(1L, BigDecimal.TEN, BigDecimal.valueOf(5));
        when(orderRepository.findAllById(List.of(1L))).thenReturn(List.of(o));
        when(multipedidoRepository.count()).thenReturn(0L);
        when(multipedidoRepository.findByNumero("MP-00001")).thenReturn(Optional.empty());
        when(orderRepository.saveAll(anyList())).thenReturn(List.of(o));
        when(multipedidoRepository.save(any(Multipedido.class))).thenAnswer(inv -> {
            Multipedido m = inv.getArgument(0);
            m.setId(1L);
            m.setCreatedAt(LocalDateTime.now());
            m.setUpdatedAt(LocalDateTime.now());
            return m;
        });
        when(orderMapper.toResponse(any(Order.class))).thenReturn(org.mockito.Mockito.mock(OrderResponse.class));

        MultipedidoResponse resp = service.createMultipedido(req);

        assertNotNull(resp);
        assertEquals("MP-00001", resp.numero());
        verify(orderRepository).saveAll(List.of(o));
        verify(multipedidoRepository).save(any(Multipedido.class));
    }

    @Test
    void createMultipedido_exitoso_ajustaNumeracionCuandoExiste() {
        CreateMultipedidoRequest req = CreateMultipedidoRequest.builder().orderIds(List.of(1L)).build();
        Order o = order(1L, BigDecimal.ZERO, BigDecimal.ZERO);
        when(orderRepository.findAllById(List.of(1L))).thenReturn(List.of(o));
        when(multipedidoRepository.count()).thenReturn(1L);
        when(multipedidoRepository.findByNumero("MP-00002")).thenReturn(Optional.of(Multipedido.builder().id(99L).numero("MP-00002").build()));
        when(multipedidoRepository.findByNumero("MP-00003")).thenReturn(Optional.empty());
        when(orderRepository.saveAll(anyList())).thenReturn(List.of(o));
        when(multipedidoRepository.save(any(Multipedido.class))).thenAnswer(inv -> {
            Multipedido m = inv.getArgument(0);
            m.setId(2L);
            m.setCreatedAt(LocalDateTime.now());
            m.setUpdatedAt(LocalDateTime.now());
            return m;
        });
        when(orderMapper.toResponse(any(Order.class))).thenReturn(org.mockito.Mockito.mock(OrderResponse.class));

        MultipedidoResponse resp = service.createMultipedido(req);

        assertNotNull(resp);
        assertEquals("MP-00003", resp.numero());
    }

    @Test
    void findAllRetornaListaOrdenada() {
        Multipedido m = Multipedido.builder().id(1L).numero("MP-00001").status("PENDIENTE")
                .orders(List.of(order(1L, null, null)))
                .build();
        when(multipedidoRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(m));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(org.mockito.Mockito.mock(OrderResponse.class));

        List<MultipedidoResponse> list = service.findAll();

        assertEquals(1, list.size());
        assertNotNull(list.get(0));
    }

    @Test
    void findByIdEncontradoYNoEncontrado() {
        Multipedido m = Multipedido.builder().id(1L).numero("MP-00001").status("PENDIENTE")
                .orders(new ArrayList<>())
                .build();
        when(multipedidoRepository.findById(1L)).thenReturn(Optional.of(m));
        org.mockito.Mockito.lenient().when(orderMapper.toResponse(any(Order.class))).thenReturn(org.mockito.Mockito.mock(OrderResponse.class));

        assertNotNull(service.findById(1L));

        when(multipedidoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void deleteExitosoYNoEncontrado() {
        Order o = order(1L, BigDecimal.ONE, BigDecimal.ONE);
        Multipedido m = Multipedido.builder().id(1L).numero("MP-00001").status("PENDIENTE")
                .orders(new ArrayList<>(List.of(o)))
                .build();
        when(multipedidoRepository.findById(1L)).thenReturn(Optional.of(m));
        when(orderRepository.saveAll(anyList())).thenReturn(List.of(o));

        service.delete(1L);

        verify(orderRepository).saveAll(m.getOrders());
        verify(multipedidoRepository).delete(m);

        when(multipedidoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }
}