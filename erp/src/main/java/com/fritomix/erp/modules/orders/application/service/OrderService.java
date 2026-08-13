package com.fritomix.erp.modules.orders.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.auth.domain.enums.RoleType;
import com.fritomix.erp.modules.customers.domain.entity.Customer;
import com.fritomix.erp.modules.customers.domain.repository.CustomerRepository;
import com.fritomix.erp.modules.notifications.application.dto.request.NotificationRequest;
import com.fritomix.erp.modules.notifications.application.service.EmailService;
import com.fritomix.erp.modules.notifications.application.service.NotificationService;
import com.fritomix.erp.modules.orders.application.dto.request.OrderRequest;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.customers.domain.entity.CustomerAddress;
import com.fritomix.erp.modules.customers.domain.repository.CustomerAddressRepository;
import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse;
import com.fritomix.erp.modules.orders.application.mapper.OrderMapper;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.entity.OrderDetail;
import com.fritomix.erp.modules.orders.domain.repository.OrderDetailRepository;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.products.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private static final Set<String> ALLOWED_ORDER_STATUS = Set.of("PENDIENTE", "APROBADO", "CANCELADO");
    private static final Set<String> CLOSED_ORDER_STATUS = Set.of("APROBADO", "CANCELADO");
    private static final String STATUS_PENDIENTE = "PENDIENTE";
    private static final String STATUS_APROBADO = "APROBADO";
    private static final String STATUS_CANCELADO = "CANCELADO";

    private final OrderRepository orderRepository;
    private final OrderDetailRepository detailRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CustomerAddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderMapper mapper;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        List<Order> orders = orderRepository.findAll();
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
        return orders.stream()
                .map(o -> mapper.toResponse(o, addressMap.get(o.getCustomer().getId()), userMap.get(o.getUserId())))
                .collect(Collectors.toList());
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
                .status(request.status() != null ? request.status() : STATUS_PENDIENTE)
                .notes(request.notes())
                .build();

        BigDecimal pesoTotal = BigDecimal.ZERO;
        BigDecimal totalBultos = BigDecimal.ZERO;

        if (request.details() != null) {
            for (OrderRequest.OrderDetailRequest dto : request.details()) {
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
        }

        order.setPesoTotalCargue(pesoTotal);
        order.setTotal(totalBultos);
        order = orderRepository.save(order);

        try {
            // Notificar a CARTERA (aprobación) y ADMIN
            notificationService.createForRoles(
                    "Nuevo pedido",
                    "Se ha creado el pedido " + order.getOrderNumber() + " para el cliente " + customer.getBusinessName() + ".",
                    "INFO",
                    "/pedidos/" + order.getId(),
                    RoleType.CARTERA, RoleType.ADMIN
            );

            // Notificar al usuario que creó el pedido
            if (request.userId() != null) {
                notificationService.create(NotificationRequest.builder()
                        .userId(request.userId())
                        .title("Pedido creado")
                        .message("El pedido " + order.getOrderNumber() + " fue creado exitosamente.")
                        .type("SUCCESS")
                        .link("/pedidos/" + order.getId())
                        .build());
            }

            // Email al cliente
            if (customer.getEmail() != null && !customer.getEmail().isBlank()) {
                emailService.sendEmail(
                        customer.getEmail(),
                        "Nuevo pedido - FritoMix",
                        "Hola " + customer.getBusinessName() + ",\n\n"
                                + "Se ha creado un nuevo pedido con el número: " + order.getOrderNumber() + ".\n\n"
                                + "Gracias por su preferencia.\n\nFritoMix S.A.S."
                );
            }
        } catch (Exception e) {
            log.error("Error al crear notificación/email para pedido {}: {}", order.getOrderNumber(), e.getMessage(), e);
        }

        return mapper.toResponse(order);
    }

    @Transactional
    public OrderResponse update(Long id, OrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        if (!STATUS_PENDIENTE.equals(order.getStatus())) {
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
            order.getDetails().clear();
            BigDecimal pesoTotal = BigDecimal.ZERO;
            BigDecimal totalBultos = BigDecimal.ZERO;
            for (OrderRequest.OrderDetailRequest dto : request.details()) {
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

        order = orderRepository.save(order);
        return mapper.toResponse(order);
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        if (CLOSED_ORDER_STATUS.contains(order.getStatus())) {
            throw new IllegalArgumentException("No se puede eliminar un pedido en estado " + order.getStatus());
        }
        orderRepository.delete(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        String newStatus = status != null ? status.toUpperCase() : null;
        if (!ALLOWED_ORDER_STATUS.contains(newStatus)) {
            throw new IllegalArgumentException("Estado inválido para el pedido: " + status);
        }
        if (!validOrderTransition(order.getStatus(), newStatus)) {
            throw new IllegalArgumentException("No se puede cambiar el pedido de " + order.getStatus() + " a " + newStatus);
        }
        order.setStatus(newStatus);
        order = orderRepository.save(order);

        try {
            if (STATUS_APROBADO.equals(newStatus)) {
                notificationService.createForRoles(
                        "Pedido aprobado",
                        "El pedido " + order.getOrderNumber() + " fue aprobado y está listo para despacho.",
                        "SUCCESS",
                        "/pedidos/" + order.getId(),
                        RoleType.DESPACHADOR, RoleType.ADMIN
                );
            } else if ("CANCELADO".equals(newStatus)) {
                notificationService.createForRoles(
                        "Pedido cancelado",
                        "El pedido " + order.getOrderNumber() + " fue cancelado.",
                        "WARNING",
                        "/pedidos/" + order.getId(),
                        RoleType.ADMIN
                );
            }
        } catch (Exception e) {
            log.error("Error al notificar cambio de estado del pedido {}: {}", order.getOrderNumber(), e.getMessage(), e);
        }

        return mapper.toResponse(order);
    }

    private boolean validOrderTransition(String current, String next) {
        if (current.equals(next)) {
            return true;
        }
        return switch (current) {
            case STATUS_PENDIENTE -> next.equals(STATUS_APROBADO) || next.equals(STATUS_CANCELADO);
            case STATUS_APROBADO -> next.equals(STATUS_CANCELADO);
            default -> false;
        };
    }
}
