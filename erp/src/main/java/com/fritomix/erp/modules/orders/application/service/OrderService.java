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
import com.fritomix.erp.modules.orders.application.dto.request.OrderRequest;
import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse;
import com.fritomix.erp.modules.orders.application.mapper.OrderMapper;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderMapper mapper;
    private final OrderDetailCalculator detailCalculator;
    private final OrderNotifier orderNotifier;

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> findAll(String search, String status, List<String> statuses, Pageable pageable) {
        String term = StringUtils.hasText(search) ? "%" + search.trim() + "%" : null;
        String statusFilter = StringUtils.hasText(status) ? status.trim().toUpperCase() : null;
        List<String> statusFilters = statuses != null && !statuses.isEmpty()
                ? statuses.stream().map(s -> s.trim().toUpperCase()).toList()
                : null;
        if (statusFilter != null && !OrderStatusRules.isValidStatus(statusFilter)) {
            throw new IllegalArgumentException("Estado inválido para el pedido: " + status);
        }
        if (statusFilters != null && statusFilters.stream().anyMatch(s -> !OrderStatusRules.isValidStatus(s))) {
            throw new IllegalArgumentException("Estado inválido para el pedido: " + statuses);
        }
        Page<Order> page = orderRepository.search(term, statusFilter, statusFilters, pageable);
        List<Order> orders = page.getContent();
        List<Long> customerIds = orders.stream()
                .map(o -> o.getCustomer().getId())
                .distinct()
                .collect(Collectors.toList());
        Map<Long, CustomerAddress> addressMap = addressRepository.findAllMainByCustomerIds(customerIds)
                .stream()
                .collect(Collectors.toMap(a -> a.getCustomer().getId(), a -> a));
        List<Long> userIds = orders.stream()
                .map(Order::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        return PageResponse.from(page, o -> mapper.toResponse(o, addressMap.get(o.getCustomer().getId()), userMap.get(o.getUserId())));
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        return mapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public String generateNextOrderNumber() {
        int max = orderRepository.maxOrderNumber();
        return String.format("PED-%05d", max + 1);
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        String orderNumber = request.orderNumber() != null && !request.orderNumber().isBlank()
                ? request.orderNumber().trim()
                : generateNextOrderNumber();
        if (orderRepository.existsByOrderNumber(orderNumber)) {
            throw new IllegalArgumentException("Ya existe un pedido con el número: " + orderNumber);
        }

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + request.customerId()));

        Order order = Order.builder()
                .customer(customer)
                .userId(request.userId())
                .orderNumber(orderNumber)
                .orderDate(request.orderDate() != null ? request.orderDate() : java.time.LocalDateTime.now())
                .status(request.status() != null ? request.status() : OrderStatusRules.STATUS_PENDIENTE)
                .notes(request.notes())
                .build();

        if (request.details() != null) {
            detailCalculator.applyDetails(order, request.details());
        } else {
            order.setPesoTotalCargue(java.math.BigDecimal.ZERO);
            order.setTotal(java.math.BigDecimal.ZERO);
        }

        order = orderRepository.save(order);
        orderNotifier.notifyCreated(order, customer, request.userId());

        return mapper.toResponse(order);
    }

    @Transactional
    public OrderResponse update(Long id, OrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        if (!OrderStatusRules.STATUS_PENDIENTE.equals(order.getStatus())) {
            throw new IllegalArgumentException("Solo se pueden editar pedidos en estado PENDIENTE");
        }

        if (request.customerId() != null && !request.customerId().equals(order.getCustomer().getId())) {
            Customer customer = customerRepository.findById(request.customerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + request.customerId()));
            order.setCustomer(customer);
        }

        if (request.orderNumber() != null && !request.orderNumber().equals(order.getOrderNumber())) {
            if (orderRepository.existsByOrderNumber(request.orderNumber())) {
                throw new IllegalArgumentException("Ya existe otro pedido con el número: " + request.orderNumber());
            }
            order.setOrderNumber(request.orderNumber());
        }

        if (request.userId() != null) order.setUserId(request.userId());
        if (request.status() != null && !request.status().equalsIgnoreCase(order.getStatus())) {
            throw new IllegalArgumentException("El estado del pedido solo cambia por aprobación o cancelación");
        }
        if (request.notes() != null) order.setNotes(request.notes());

        if (request.details() != null) {
            detailCalculator.applyDetails(order, request.details());
        }

        order = orderRepository.save(order);
        return mapper.toResponse(order);
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        if (OrderStatusRules.isClosed(order.getStatus())) {
            throw new IllegalArgumentException("No se puede eliminar un pedido en estado " + order.getStatus());
        }
        orderRepository.delete(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        String newStatus = status != null ? status.toUpperCase() : null;
        if (!OrderStatusRules.isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Estado inválido para el pedido: " + status);
        }
        if (!OrderStatusRules.canTransition(order.getStatus(), newStatus)) {
            throw new IllegalArgumentException("No se puede cambiar el pedido de " + order.getStatus() + " a " + newStatus);
        }
        order.setStatus(newStatus);
        if ("APROBADO".equals(newStatus)) {
            order.setApprovedById(currentUserId());
            order.setApprovedAt(LocalDateTime.now());
        }
        order = orderRepository.save(order);

        orderNotifier.notifyStatusChanged(order, newStatus);

        return mapper.toResponse(order);
    }

    @Transactional
    public OrderResponse updateProductionStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        String newStatus = status != null ? status.toUpperCase() : null;
        if (newStatus == null || !OrderStatusRules.isProductionStatus(newStatus)) {
            throw new IllegalArgumentException("Estado de producción inválido: " + status);
        }
        if (!OrderStatusRules.canTransition(order.getStatus(), newStatus)) {
            throw new IllegalArgumentException("No se puede cambiar el pedido de " + order.getStatus() + " a " + newStatus);
        }
        order.setStatus(newStatus);
        order = orderRepository.save(order);

        orderNotifier.notifyStatusChanged(order, newStatus);

        return mapper.toResponse(order);
    }

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserInfo userInfo) {
            return userInfo.userId();
        }
        return null;
    }
}