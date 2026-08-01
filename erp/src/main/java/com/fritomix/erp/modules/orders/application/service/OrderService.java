package com.fritomix.erp.modules.orders.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

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

    @Transactional
    public OrderResponse create(OrderRequest request) {
        if (orderRepository.existsByOrderNumber(request.orderNumber())) {
            throw new IllegalArgumentException("Ya existe un pedido con el número: " + request.orderNumber());
        }

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + request.customerId()));

        Order order = Order.builder()
                .customer(customer)
                .userId(request.userId())
                .orderNumber(request.orderNumber())
                .orderDate(request.orderDate() != null ? request.orderDate() : java.time.LocalDateTime.now())
                .status(request.status() != null ? request.status() : "PENDIENTE")
                .total(request.total())
                .notes(request.notes())
                .build();

        BigDecimal pesoTotal = BigDecimal.ZERO;

        if (request.details() != null) {
            for (OrderRequest.OrderDetailRequest dto : request.details()) {
                Product product = productRepository.findById(dto.productId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + dto.productId()));

                OrderDetail detail = OrderDetail.builder()
                        .order(order)
                        .product(product)
                        .quantity(dto.quantity())
                        .price(BigDecimal.ZERO)
                        .subtotal(BigDecimal.ZERO)
                        .build();
                order.getDetails().add(detail);

                if (product.getPesoTotalCargue() != null) {
                    pesoTotal = pesoTotal.add(product.getPesoTotalCargue().multiply(dto.quantity()));
                }
            }
        }

        order.setPesoTotalCargue(pesoTotal);
        order = orderRepository.save(order);

        try {
            if (request.userId() != null) {
                notificationService.create(NotificationRequest.builder()
                        .userId(request.userId())
                        .title("Pedido creado")
                        .message("El pedido " + order.getOrderNumber() + " fue creado exitosamente.")
                        .type("SUCCESS")
                        .link("/pedidos/" + order.getId())
                        .build());
            }

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
        if (request.status() != null) order.setStatus(request.status());
        if (request.total() != null) order.setTotal(request.total());
        if (request.notes() != null) order.setNotes(request.notes());

        if (request.details() != null) {
            order.getDetails().clear();
            BigDecimal pesoTotal = BigDecimal.ZERO;
            for (OrderRequest.OrderDetailRequest dto : request.details()) {
                Product product = productRepository.findById(dto.productId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + dto.productId()));
                OrderDetail detail = OrderDetail.builder()
                        .order(order)
                        .product(product)
                        .quantity(dto.quantity())
                        .price(BigDecimal.ZERO)
                        .subtotal(BigDecimal.ZERO)
                        .build();
                order.getDetails().add(detail);

                if (product.getPesoTotalCargue() != null) {
                    pesoTotal = pesoTotal.add(product.getPesoTotalCargue().multiply(dto.quantity()));
                }
            }
            order.setPesoTotalCargue(pesoTotal);
        }

        order = orderRepository.save(order);
        return mapper.toResponse(order);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido no encontrado con id: " + id);
        }
        orderRepository.deleteById(id);
    }
}
