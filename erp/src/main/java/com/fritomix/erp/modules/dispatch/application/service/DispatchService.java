package com.fritomix.erp.modules.dispatch.application.service;

import com.fritomix.erp.exception.PedidoYaDespachadoException;
import com.fritomix.erp.exception.ProductoNotFoundException;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.auth.domain.enums.RoleType;
import java.math.BigDecimal;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.dispatch.application.dto.request.DispatchRequest;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse;
import com.fritomix.erp.modules.dispatch.application.mapper.DispatchMapper;
import com.fritomix.erp.modules.dispatch.domain.entity.Arrume;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.dispatch.domain.repository.DispatchRepository;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.drivers.domain.repository.DriverRepository;
import com.fritomix.erp.modules.notifications.application.dto.request.NotificationRequest;
import com.fritomix.erp.modules.notifications.application.service.EmailService;
import com.fritomix.erp.modules.notifications.application.service.NotificationService;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.products.domain.repository.ProductRepository;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import com.fritomix.erp.modules.vehicles.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DispatchService {

    private static final Set<String> VALID_TIPO_PEDIDO = Set.of("pedido_unico", "pedido_multipedido");
    private static final Set<String> VALID_STATUS = Set.of(
            "PENDIENTE", "ELABORACION", "PRODUCCION", "LISTO_CARGUE", "DESPACHADO");
    private static final List<String> STATUS_FLOW = List.of(
            "PENDIENTE", "ELABORACION", "PRODUCCION", "LISTO_CARGUE", "DESPACHADO");
    private static final Set<String> STATUS_CERRADOS = Set.of("DESPACHADO");
    private static final String CUMPLIMIENTO_COMPLETO = "COMPLETO";
    private static final String CUMPLIMIENTO_PARCIAL = "PARCIAL";

    private final DispatchRepository dispatchRepository;
    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final ProductRepository productRepository;
    private final DispatchMapper mapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public List<DispatchResponse> findAll() {
        return dispatchRepository.findAllWithFetch().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DispatchResponse findById(Long id) {
        Dispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));
        return mapper.toResponse(dispatch);
    }

    @Transactional
    public DispatchResponse create(DispatchRequest request) {
        if (dispatchRepository.existsByDispatchNumber(request.dispatchNumber())) {
            throw new IllegalArgumentException("Ya existe un despacho con el número: " + request.dispatchNumber());
        }

        String tipoPedido = request.tipoPedido() != null ? request.tipoPedido() : "pedido_unico";
        if (!VALID_TIPO_PEDIDO.contains(tipoPedido)) {
            throw new IllegalArgumentException("tipo_pedido inválido. Valores válidos: " + VALID_TIPO_PEDIDO);
        }

        List<Order> orders = resolveOrders(request);
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un pedido para el despacho");
        }
        if ("pedido_unico".equals(tipoPedido) && orders.size() != 1) {
            throw new IllegalArgumentException("Un despacho de tipo pedido_unico debe contener exactamente un pedido");
        }

        validateNoActiveDispatch(orders);

        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id: " + request.driverId()));
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con id: " + request.vehicleId()));

        Dispatch dispatch = Dispatch.builder()
                .tipoPedido(tipoPedido)
                .orders(orders)
                .driver(driver)
                .vehicle(vehicle)
                .dispatchNumber(request.dispatchNumber())
                .dispatchDate(request.dispatchDate() != null ? request.dispatchDate() : java.time.LocalDateTime.now())
                .status(request.status() != null ? request.status() : "PENDIENTE")
                .notes(request.notes())
                .userId(request.userId())
                .build();

        if (request.details() != null) {
            for (DispatchRequest.DispatchDetailRequest dto : request.details()) {
                Product product = findProduct(dto.productId());
                DispatchDetail detail = DispatchDetail.builder()
                        .dispatch(dispatch)
                        .product(product)
                        .quantity(dto.quantity())
                        .delivered(dto.delivered() != null ? dto.delivered() : dto.quantity())
                        .observations(dto.observations())
                        .detalleProducto(dto.detalleProducto())
                        .lote(dto.lote())
                        .build();
                dispatch.getDetails().add(detail);
            }
        }

        if (request.arrumes() != null) {
            for (DispatchRequest.ArrumeRequest dto : request.arrumes()) {
                Arrume arrume = Arrume.builder()
                        .dispatch(dispatch)
                        .numArrume(dto.numArrume())
                        .arrumeProducto(dto.arrumeProducto())
                        .cantidad(dto.cantidad())
                        .lote(dto.lote())
                        .build();
                dispatch.getArrumes().add(arrume);
            }
        }

        dispatch.setCumplimiento(calcularCumplimiento(dispatch.getDetails()));

        dispatch = dispatchRepository.save(dispatch);
        notifyCreated(dispatch, orders, driver, vehicle, request.userId());

        return mapper.toResponse(dispatch);
    }

    @Transactional
    public DispatchResponse update(Long id, DispatchRequest request) {
        Dispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));

        String tipoPedido = request.tipoPedido() != null ? request.tipoPedido() : dispatch.getTipoPedido();
        if (!VALID_TIPO_PEDIDO.contains(tipoPedido)) {
            throw new IllegalArgumentException("tipo_pedido inválido. Valores válidos: " + VALID_TIPO_PEDIDO);
        }
        dispatch.setTipoPedido(tipoPedido);

        if (request.orderIds() != null || request.orderId() != null) {
            List<Order> orders = resolveOrders(request);
            if (orders.isEmpty()) {
                throw new IllegalArgumentException("Debe seleccionar al menos un pedido para el despacho");
            }
            validateNoActiveDispatch(orders, dispatch.getId());
            dispatch.getOrders().clear();
            dispatch.getOrders().addAll(orders);
        }

        if (request.driverId() != null && !request.driverId().equals(dispatch.getDriver().getId())) {
            Driver driver = driverRepository.findById(request.driverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id: " + request.driverId()));
            dispatch.setDriver(driver);
        }
        if (request.vehicleId() != null && !request.vehicleId().equals(dispatch.getVehicle().getId())) {
            Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con id: " + request.vehicleId()));
            dispatch.setVehicle(vehicle);
        }

        if (request.dispatchNumber() != null) dispatch.setDispatchNumber(request.dispatchNumber());
        if (request.status() != null) dispatch.setStatus(request.status());
        if (request.notes() != null) dispatch.setNotes(request.notes());

        if (request.details() != null) {
            dispatch.getDetails().clear();
            for (DispatchRequest.DispatchDetailRequest dto : request.details()) {
                Product product = findProduct(dto.productId());
                DispatchDetail detail = DispatchDetail.builder()
                        .dispatch(dispatch)
                        .product(product)
                        .quantity(dto.quantity())
                        .delivered(dto.delivered() != null ? dto.delivered() : dto.quantity())
                        .observations(dto.observations())
                        .detalleProducto(dto.detalleProducto())
                        .lote(dto.lote())
                        .build();
                dispatch.getDetails().add(detail);
            }
            dispatch.setCumplimiento(calcularCumplimiento(dispatch.getDetails()));
        }

        if (request.arrumes() != null) {
            dispatch.getArrumes().clear();
            for (DispatchRequest.ArrumeRequest dto : request.arrumes()) {
                Arrume arrume = Arrume.builder()
                        .dispatch(dispatch)
                        .numArrume(dto.numArrume())
                        .arrumeProducto(dto.arrumeProducto())
                        .cantidad(dto.cantidad())
                        .lote(dto.lote())
                        .build();
                dispatch.getArrumes().add(arrume);
            }
        }

        dispatch = dispatchRepository.save(dispatch);
        return mapper.toResponse(dispatch);
    }

    @Transactional
    public void delete(Long id) {
        if (!dispatchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Despacho no encontrado con id: " + id);
        }
        dispatchRepository.deleteById(id);
    }

    @Transactional
    public DispatchResponse updateStatus(Long id, String status) {
        Dispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));
        String newStatus = status != null ? status.toUpperCase() : null;
        if (!VALID_STATUS.contains(newStatus)) {
            throw new IllegalArgumentException("Estado inválido para el despacho: " + status);
        }
        dispatch.setStatus(newStatus);
        dispatch = dispatchRepository.save(dispatch);

        try {
            if ("DESPACHADO".equals(newStatus)) {
                String orderNumbers = dispatch.getOrders().stream()
                        .map(Order::getOrderNumber)
                        .collect(Collectors.joining(", "));
                notificationService.createForRoles(
                        "Despacho despachado",
                        "El despacho " + dispatch.getDispatchNumber() + " fue despachado (pedidos: " + orderNumbers + ").",
                        "SUCCESS",
                        "/despachos/" + dispatch.getId(),
                        RoleType.CARTERA, RoleType.ADMIN
                );
            }
        } catch (Exception e) {
            log.error("Error al notificar cambio de estado del despacho {}: {}", dispatch.getDispatchNumber(), e.getMessage(), e);
        }

        return mapper.toResponse(dispatch);
    }

    @Transactional(readOnly = true)
    public List<DispatchResponse> findHistoryByOrderId(Long orderId) {
        return dispatchRepository.findAllByOrderId(orderId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DispatchResponse> findByDateRange(java.time.LocalDateTime desde, java.time.LocalDateTime hasta) {
        return dispatchRepository.findAllBetweenDates(desde, hasta).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con id: " + productId));
    }

    private void validateNoActiveDispatch(List<Order> orders) {
        validateNoActiveDispatch(orders, null);
    }

    private void validateNoActiveDispatch(List<Order> orders, Long excludeDispatchId) {
        for (Order order : orders) {
            List<Dispatch> active = dispatchRepository.findActiveByOrderId(order.getId(), STATUS_CERRADOS);
            for (Dispatch d : active) {
                if (excludeDispatchId != null && d.getId().equals(excludeDispatchId)) {
                    continue;
                }
                throw new PedidoYaDespachadoException(
                        "El pedido " + order.getOrderNumber() + " ya tiene un despacho activo: " + d.getDispatchNumber());
            }
        }
    }

    private String calcularCumplimiento(List<DispatchDetail> details) {
        if (details == null || details.isEmpty()) {
            return CUMPLIMIENTO_PARCIAL;
        }
        boolean parcial = false;
        for (DispatchDetail detail : details) {
            BigDecimal solicitado = detail.getQuantity() != null ? detail.getQuantity() : BigDecimal.ZERO;
            BigDecimal despachado = detail.getDelivered() != null ? detail.getDelivered() : BigDecimal.ZERO;
            if (despachado.compareTo(solicitado) < 0) {
                parcial = true;
                break;
            }
        }
        return parcial ? CUMPLIMIENTO_PARCIAL : CUMPLIMIENTO_COMPLETO;
    }

    private List<Order> resolveOrders(DispatchRequest request) {
        List<Long> ids = request.orderIds();
        if (ids == null || ids.isEmpty()) {
            if (request.orderId() != null) {
                ids = List.of(request.orderId());
            } else {
                return List.of();
            }
        }

        List<Order> orders = orderRepository.findAllById(ids);
        if (orders.size() != ids.size()) {
            throw new ResourceNotFoundException("Uno o más pedidos no fueron encontrados");
        }
        return orders;
    }

    private void notifyCreated(Dispatch dispatch, List<Order> orders, Driver driver, Vehicle vehicle, Long userId) {
        String orderNumbers = orders.stream()
                .map(Order::getOrderNumber)
                .collect(Collectors.joining(", "));
        try {
            notificationService.createForRoles(
                    "Nuevo despacho",
                    "Se ha creado el despacho " + dispatch.getDispatchNumber() + " para el/los pedido(s): " + orderNumbers + ".",
                    "INFO",
                    "/despachos/" + dispatch.getId(),
                    RoleType.CARTERA, RoleType.ADMIN
            );

            if (userId != null) {
                notificationService.create(NotificationRequest.builder()
                        .userId(userId)
                        .title("Despacho creado")
                        .message("El despacho " + dispatch.getDispatchNumber() + " fue creado exitosamente.")
                        .type("SUCCESS")
                        .link("/despachos/" + dispatch.getId())
                        .build());

                User dispatchUser = userRepository.findById(userId).orElse(null);
                if (dispatchUser != null && dispatchUser.getEmail() != null && !dispatchUser.getEmail().isBlank()) {
                    emailService.sendEmail(
                            dispatchUser.getEmail(),
                            "Nuevo despacho - FritoMix",
                            "Hola " + dispatchUser.getFirstName() + ",\n\n"
                                    + "Se ha creado un nuevo despacho: " + dispatch.getDispatchNumber() + ".\n"
                                    + "Conductor: " + driver.getName() + "\n"
                                    + "Vehículo: " + vehicle.getVehicleNumber() + "\n\n"
                                    + "FritoMix S.A.S."
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error al crear notificación/email para despacho {}: {}", dispatch.getDispatchNumber(), e.getMessage(), e);
        }
    }

}
