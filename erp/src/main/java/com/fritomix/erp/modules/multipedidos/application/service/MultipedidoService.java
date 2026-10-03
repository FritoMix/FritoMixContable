package com.fritomix.erp.modules.multipedidos.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.multipedidos.application.dto.request.CreateMultipedidoRequest;
import com.fritomix.erp.modules.multipedidos.application.dto.response.MultipedidoResponse;
import com.fritomix.erp.modules.multipedidos.domain.entity.Multipedido;
import com.fritomix.erp.modules.multipedidos.domain.repository.MultipedidoRepository;
import com.fritomix.erp.modules.orders.application.mapper.OrderMapper;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MultipedidoService {

    private final MultipedidoRepository multipedidoRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public MultipedidoResponse createMultipedido(CreateMultipedidoRequest request) {
        if (request.getOrderIds() == null || request.getOrderIds().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un pedido.");
        }

        List<Order> orders = orderRepository.findAllById(request.getOrderIds());
        if (orders.size() != request.getOrderIds().size()) {
            throw new ResourceNotFoundException("Uno o más pedidos no fueron encontrados.");
        }

        // Generate unique number, e.g., MP-00001
        long nextId = multipedidoRepository.count() + 1;
        String numero = String.format("MP-%05d", nextId);
        while (multipedidoRepository.findByNumero(numero).isPresent()) {
            nextId++;
            numero = String.format("MP-%05d", nextId);
        }

        // Mark orders as multipedido
        for (Order order : orders) {
            order.setTipoPedido("pedido_multipedido");
        }
        orderRepository.saveAll(orders);

        Multipedido multipedido = Multipedido.builder()
                .numero(numero)
                .status("PENDIENTE")
                .orders(orders)
                .build();

        Multipedido saved = multipedidoRepository.save(multipedido);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MultipedidoResponse> findAll() {
        return multipedidoRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MultipedidoResponse findById(Long id) {
        Multipedido multipedido = multipedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Multipedido no encontrado con id: " + id));
        return toResponse(multipedido);
    }

    @Transactional
    public void delete(Long id) {
        Multipedido multipedido = multipedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Multipedido no encontrado con id: " + id));

        // Revert orders back to pedido_unico
        for (Order order : multipedido.getOrders()) {
            order.setTipoPedido("pedido_unico");
        }
        orderRepository.saveAll(multipedido.getOrders());

        multipedidoRepository.delete(multipedido);
    }

    private MultipedidoResponse toResponse(Multipedido multipedido) {
        BigDecimal totalPeso = multipedido.getOrders().stream()
                .map(o -> o.getPesoTotalCargue() != null ? o.getPesoTotalCargue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMonto = multipedido.getOrders().stream()
                .map(o -> o.getTotal() != null ? o.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return MultipedidoResponse.builder()
                .id(multipedido.getId())
                .numero(multipedido.getNumero())
                .status(multipedido.getStatus())
                .totalPeso(totalPeso)
                .totalMonto(totalMonto)
                .orders(multipedido.getOrders().stream().map(orderMapper::toResponse).toList())
                .createdAt(multipedido.getCreatedAt())
                .updatedAt(multipedido.getUpdatedAt())
                .build();
    }
}
