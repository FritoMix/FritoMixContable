package com.fritomix.erp.modules.dispatch.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.exception.PedidoYaDespachadoException;
import com.fritomix.erp.exception.ProductoNotFoundException;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.auth.application.dto.JwtUserInfo;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.dispatch.application.dto.request.ConfirmarPlacaRequest;
import com.fritomix.erp.modules.dispatch.application.dto.request.DispatchRequest;
import com.fritomix.erp.modules.dispatch.application.dto.response.DespachadorDto;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse;
import com.fritomix.erp.modules.dispatch.application.mapper.DispatchMapper;
import com.fritomix.erp.modules.dispatch.domain.entity.Arrume;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.dispatch.domain.repository.DispatchRepository;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.drivers.domain.repository.DriverRepository;
import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse;
import com.fritomix.erp.modules.orders.application.mapper.OrderMapper;
import com.fritomix.erp.modules.orders.application.service.OrderStatusRules;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.products.domain.repository.ProductRepository;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import com.fritomix.erp.modules.vehicles.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final ProductRepository productRepository;
    private final DispatchMapper mapper;
    private final OrderMapper orderMapper;
    private final DispatchNotifier dispatchNotifier;
    private final UserRepository userRepository;
    private final EntityManager em;

    private void saveFacturasPorPedido(Long dispatchId, List<DispatchRequest.OrderFacturaRequest> orderFacturas) {
        if (orderFacturas == null || orderFacturas.isEmpty()) return;
        for (DispatchRequest.OrderFacturaRequest of : orderFacturas) {
            if (of.orderId() != null) {
                em.createNativeQuery("UPDATE dispatch_orders SET numero_factura = :numeroFactura WHERE dispatch_id = :dispatchId AND order_id = :orderId")
                        .setParameter("numeroFactura", of.numeroFactura())
                        .setParameter("dispatchId", dispatchId)
                        .setParameter("orderId", of.orderId())
                        .executeUpdate();
            }
        }
    }

    private Map<Long, String> loadFacturasPorPedido(Long dispatchId) {
        Map<Long, String> result = new HashMap<>();
        if (dispatchId == null) return result;
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery("SELECT order_id, numero_factura FROM dispatch_orders WHERE dispatch_id = :dispatchId")
                .setParameter("dispatchId", dispatchId)
                .getResultList();
        for (Object[] row : rows) {
            if (row[0] != null) {
                Long orderId = ((Number) row[0]).longValue();
                String factura = (String) row[1];
                result.put(orderId, factura);
            }
        }
        return result;
    }

    private DispatchResponse toResponseWithFacturas(Dispatch dispatch) {
        dispatch.setFacturasPorPedido(loadFacturasPorPedido(dispatch.getId()));
        return mapper.toResponse(dispatch);
    }

    @Transactional(readOnly = true)
    public PageResponse<DispatchResponse> findAll(String search, List<String> statuses, Pageable pageable) {
        String term = StringUtils.hasText(search) ? "%" + search.trim() + "%" : null;
        List<String> estados = (statuses == null || statuses.isEmpty()) ? null : statuses;
        Page<Long> ids = (estados == null)
                ? dispatchRepository.findIds(term, pageable)
                : dispatchRepository.findIdsByStatuses(term, estados, pageable);
        if (ids.isEmpty()) {
            return PageResponse.of(List.of(), ids.getNumber(), ids.getSize(), ids.getTotalElements(), ids.getTotalPages());
        }
        Map<Long, Dispatch> byId = dispatchRepository.findAllWithFetchByIds(ids.getContent()).stream()
                .collect(Collectors.toMap(Dispatch::getId, d -> d));
        List<DispatchResponse> content = ids.getContent().stream()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .map(this::toResponseWithFacturas)
                .toList();
        return PageResponse.of(content, ids.getNumber(), ids.getSize(), ids.getTotalElements(), ids.getTotalPages());
    }

    @Transactional(readOnly = true)
    public DispatchResponse findById(Long id) {
        Dispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));
        return toResponseWithFacturas(dispatch);
    }

    @Transactional(readOnly = true)
    public PageResponse<DispatchResponse> findAssignedToDriver(String search, Pageable pageable) {
        String term = StringUtils.hasText(search) ? "%" + search.trim() + "%" : null;
        Long userId = currentUserId();
        Page<Long> ids = dispatchRepository.findAssignedIds(term, userId, pageable);
        if (ids.isEmpty()) {
            return PageResponse.of(List.of(), ids.getNumber(), ids.getSize(), ids.getTotalElements(), ids.getTotalPages());
        }
        Map<Long, Dispatch> byId = dispatchRepository.findAllWithFetchByIds(ids.getContent()).stream()
                .collect(Collectors.toMap(Dispatch::getId, d -> d));
        List<DispatchResponse> content = ids.getContent().stream()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .map(this::toResponseWithFacturas)
                .toList();
        return PageResponse.of(content, ids.getNumber(), ids.getSize(), ids.getTotalElements(), ids.getTotalPages());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listosParaCargue() {
        return orderRepository.findReadyForDispatch().stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<DispatchResponse> findAssignedByMe(Pageable pageable) {
        Long userId = currentUserId();
        if (userId == null) {
            return PageResponse.of(List.of(), 0, pageable.getPageSize(), 0, 0);
        }
        Page<Long> ids = dispatchRepository.findIdsByUserId(userId, pageable);
        if (ids.isEmpty()) {
            return PageResponse.of(List.of(), ids.getNumber(), ids.getSize(), ids.getTotalElements(), ids.getTotalPages());
        }
        Map<Long, Dispatch> byId = dispatchRepository.findAllWithFetchByIds(ids.getContent()).stream()
                .collect(Collectors.toMap(Dispatch::getId, d -> d));
        List<DispatchResponse> content = ids.getContent().stream()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .map(this::toResponseWithFacturas)
                .toList();
        return PageResponse.of(content, ids.getNumber(), ids.getSize(), ids.getTotalElements(), ids.getTotalPages());
    }

    @Transactional(readOnly = true)
    public PageResponse<DispatchResponse> misConfirmaciones(Pageable pageable) {
        Long userId = currentUserId();
        if (userId == null) {
            return PageResponse.of(List.of(), 0, pageable.getPageSize(), 0, 0);
        }
        Page<Long> ids = dispatchRepository.findIdsByConfirmedByUserId(userId, pageable);
        if (ids.isEmpty()) {
            return PageResponse.of(List.of(), ids.getNumber(), ids.getSize(), ids.getTotalElements(), ids.getTotalPages());
        }
        Map<Long, Dispatch> byId = dispatchRepository.findAllWithFetchByIds(ids.getContent()).stream()
                .collect(Collectors.toMap(Dispatch::getId, d -> d));
        List<DispatchResponse> content = ids.getContent().stream()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .map(this::toResponseWithFacturas)
                .toList();
        return PageResponse.of(content, ids.getNumber(), ids.getSize(), ids.getTotalElements(), ids.getTotalPages());
    }

    @Transactional(readOnly = true)
    public List<DespachadorDto> despachadores() {
        return userRepository.findByRoleName("DESPACHADOR3").stream()
                .filter(u -> Boolean.TRUE.equals(u.getEnabled()))
                .map(u -> DespachadorDto.builder()
                        .id(u.getId())
                        .nombre((u.getFirstName() + " " + u.getLastName()).trim())
                        .email(u.getEmail())
                        .build())
                .toList();
    }

    @Transactional
    public DispatchResponse confirmarPlaca(Long id, ConfirmarPlacaRequest request) {
        Dispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));

        if (!DispatchFlow.STATUS_VEHICULO_ASIGNADO.equals(dispatch.getStatus())) {
            throw new IllegalArgumentException(
                    "El despacho " + dispatch.getDispatchNumber() + " debe estar en VEHICULO_ASIGNADO para confirmar la placa.");
        }

        if (request.despachadorId() == null) {
            throw new IllegalArgumentException("Debe seleccionar el despachador (Despacho 3).");
        }
        User despachador = userRepository.findById(request.despachadorId())
                .filter(u -> Boolean.TRUE.equals(u.getEnabled()))
                .orElseThrow(() -> new ResourceNotFoundException("Despachador no encontrado con id: " + request.despachadorId()));
        if (!"DESPACHADOR3".equals(despachador.getRole().getName())) {
            throw new IllegalArgumentException("El usuario seleccionado no tiene el rol Despachador 3.");
        }

        String placaNormalizada = request.placa() != null ? request.placa().trim().toUpperCase() : "";
        if (placaNormalizada.isEmpty()) {
            throw new IllegalArgumentException("Debe confirmar la placa del vehículo.");
        }

        boolean placaCambio = !placaNormalizada.equalsIgnoreCase(dispatch.getVehiclePlate());
        if (placaCambio && !StringUtils.hasText(request.observacionPlaca())) {
            throw new IllegalArgumentException("Si cambia la placa, debe registrar la observación del cambio.");
        }

        Vehicle vehicle = vehicleRepository.findByVehicleNumber(placaNormalizada).orElse(null);

        dispatch.setVehiclePlate(placaNormalizada);
        dispatch.setVehicle(vehicle);
        dispatch.setPlacaObservacion(request.observacionPlaca() != null ? request.observacionPlaca().trim() : null);
        dispatch.setDespachadorUserId(despachador.getId());
        dispatch.setConfirmadoPorUserId(currentUserId());

        DispatchFlow.validateTransition(DispatchFlow.STATUS_VEHICULO_ASIGNADO, DispatchFlow.STATUS_CONDUCTOR_ASIGNADO);
        dispatch.setStatus(DispatchFlow.STATUS_CONDUCTOR_ASIGNADO);
        dispatch = dispatchRepository.save(dispatch);

        dispatchNotifier.notifyPlacaConfirmada(dispatch, despachador);

        return toResponseWithFacturas(dispatch);
    }

    @Transactional
    public DispatchResponse asignarPlaca(Long orderId, String placa) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + orderId));

        if (!OrderStatusRules.STATUS_LISTO_PRODUCCION.equals(order.getStatus())) {
            throw new IllegalArgumentException(
                    "El pedido " + order.getOrderNumber() + " no está LISTO_PRODUCCION y no puede cargarse.");
        }

        validateNoActiveDispatch(List.of(order));

        String placaNormalizada = placa != null ? placa.trim().toUpperCase() : "";
        if (placaNormalizada.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar la placa del vehículo.");
        }
        Vehicle vehicle = vehicleRepository.findByVehicleNumber(placaNormalizada).orElse(null);

        List<Order> orders = new ArrayList<>(List.of(order));
        Dispatch dispatch = Dispatch.builder()
                .tipoPedido("pedido_unico")
                .orders(orders)
                .vehicle(vehicle)
                .vehiclePlate(placaNormalizada)
                .dispatchNumber(generarNumeroDespacho())
                .dispatchDate(LocalDateTime.now())
                .status(DispatchFlow.STATUS_PENDIENTE)
                .cumplimiento(DispatchFlow.CUMPLIMIENTO_PARCIAL)
                .userId(currentUserId())
                .build();
        dispatch = dispatchRepository.save(dispatch);

        DispatchFlow.validateTransition(DispatchFlow.STATUS_PENDIENTE, DispatchFlow.STATUS_VEHICULO_ASIGNADO);
        dispatch.setStatus(DispatchFlow.STATUS_VEHICULO_ASIGNADO);
        dispatch = dispatchRepository.save(dispatch);

        dispatchNotifier.notifyCreated(dispatch, orders, null, vehicle, dispatch.getUserId());

        return toResponseWithFacturas(dispatch);
    }

    @Transactional
    public DispatchResponse create(DispatchRequest request) {
        if (dispatchRepository.existsByDispatchNumber(request.dispatchNumber())) {
            throw new IllegalArgumentException("Ya existe un despacho con el número: " + request.dispatchNumber());
        }

        String tipoPedido = request.tipoPedido() != null ? request.tipoPedido() : "pedido_unico";
        if (!DispatchFlow.isValidTipoPedido(tipoPedido)) {
            throw new IllegalArgumentException("tipo_pedido inválido. Valores válidos: " + DispatchFlow.VALID_TIPO_PEDIDO);
        }

        List<Order> orders = resolveOrders(request);
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un pedido para el despacho");
        }
        if ("pedido_unico".equals(tipoPedido) && orders.size() != 1) {
            throw new IllegalArgumentException("Un despacho de tipo pedido_unico debe contener exactamente un pedido");
        }

        String status = DispatchFlow.initialStatus(request.status());

        validateNoActiveDispatch(orders);

        Driver driver = null;
        if (request.driverId() != null) {
            driver = driverRepository.findById(request.driverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id: " + request.driverId()));
        }
        Vehicle vehicle = null;
        if (request.vehicleId() != null) {
            vehicle = vehicleRepository.findById(request.vehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con id: " + request.vehicleId()));
        }

        Dispatch dispatch = Dispatch.builder()
                .tipoPedido(tipoPedido)
                .orders(orders)
                .driver(driver)
                .vehicle(vehicle)
                .vehiclePlate(vehicle != null ? vehicle.getVehicleNumber() : null)
                .dispatchNumber(request.dispatchNumber())
                .numeroFactura(request.numeroFactura())
                .dispatchDate(request.dispatchDate() != null ? request.dispatchDate() : LocalDateTime.now())
                .status(status)
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

        dispatch.setCumplimiento(DispatchFlow.calcularCumplimiento(dispatch.getDetails()));

        dispatch = dispatchRepository.save(dispatch);
        saveFacturasPorPedido(dispatch.getId(), request.orderFacturas());
        dispatchNotifier.notifyCreated(dispatch, orders, driver, vehicle, request.userId());

        return toResponseWithFacturas(dispatch);
    }

    @Transactional
    public DispatchResponse update(Long id, DispatchRequest request) {
        Dispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));

        if (DispatchFlow.isClosed(dispatch.getStatus())) {
            throw new IllegalArgumentException("No se puede editar un despacho en estado " + dispatch.getStatus());
        }

        String tipoPedido = request.tipoPedido() != null ? request.tipoPedido() : dispatch.getTipoPedido();
        if (!DispatchFlow.isValidTipoPedido(tipoPedido)) {
            throw new IllegalArgumentException("tipo_pedido inválido. Valores válidos: " + DispatchFlow.VALID_TIPO_PEDIDO);
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

        if (request.driverId() != null
                && (dispatch.getDriver() == null || !request.driverId().equals(dispatch.getDriver().getId()))) {
            Driver driver = driverRepository.findById(request.driverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id: " + request.driverId()));
            dispatch.setDriver(driver);
        }
        if (request.vehicleId() != null
                && (dispatch.getVehicle() == null || !request.vehicleId().equals(dispatch.getVehicle().getId()))) {
            Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con id: " + request.vehicleId()));
            dispatch.setVehicle(vehicle);
            dispatch.setVehiclePlate(vehicle.getVehicleNumber());
        }

        if (request.dispatchNumber() != null) dispatch.setDispatchNumber(request.dispatchNumber());
        if (request.numeroFactura() != null) dispatch.setNumeroFactura(request.numeroFactura());
        if (request.status() != null) {
            String newStatus = request.status().toUpperCase();
            DispatchFlow.validateTransition(dispatch.getStatus(), newStatus);
            dispatch.setStatus(newStatus);
        }
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
            dispatch.setCumplimiento(DispatchFlow.calcularCumplimiento(dispatch.getDetails()));
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
        if (request.orderFacturas() != null) {
            saveFacturasPorPedido(dispatch.getId(), request.orderFacturas());
        }
        return toResponseWithFacturas(dispatch);
    }

    @Transactional
    public void delete(Long id) {
        Dispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));
        if (DispatchFlow.isClosed(dispatch.getStatus())) {
            throw new IllegalArgumentException("No se puede eliminar un despacho en estado " + dispatch.getStatus());
        }
        dispatchRepository.delete(dispatch);
    }

    @Transactional
    public DispatchResponse updateStatus(Long id, String status) {
        Dispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));
        String newStatus = status != null ? status.toUpperCase() : null;
        if (!DispatchFlow.isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Estado inválido para el despacho: " + status);
        }
        DispatchFlow.validateTransition(dispatch.getStatus(), newStatus);
        dispatch.setStatus(newStatus);
        dispatch = dispatchRepository.save(dispatch);

        if ("DESPACHADO".equals(newStatus)) {
            dispatchNotifier.notifyDispatched(dispatch);
        }

        return toResponseWithFacturas(dispatch);
    }

    @Transactional(readOnly = true)
    public List<DispatchResponse> findHistoryByOrderId(Long orderId) {
        return dispatchRepository.findAllByOrderId(orderId).stream()
                .map(this::toResponseWithFacturas)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<DispatchResponse> findByDateRange(LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        Page<Long> ids = dispatchRepository.findIdsBetweenDates(desde, hasta, pageable);
        if (ids.isEmpty()) {
            return PageResponse.of(List.of(), ids.getNumber(), ids.getSize(), ids.getTotalElements(), ids.getTotalPages());
        }
        Map<Long, Dispatch> byId = dispatchRepository.findAllWithFetchByIds(ids.getContent()).stream()
                .collect(Collectors.toMap(Dispatch::getId, d -> d));
        List<DispatchResponse> content = ids.getContent().stream()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .map(this::toResponseWithFacturas)
                .toList();
        return PageResponse.of(content, ids.getNumber(), ids.getSize(), ids.getTotalElements(), ids.getTotalPages());
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
            for (Dispatch d : dispatchRepository.findAllByOrderId(order.getId())) {
                if (excludeDispatchId != null && d.getId().equals(excludeDispatchId)) {
                    continue;
                }
                throw new PedidoYaDespachadoException(
                        "El pedido " + order.getOrderNumber() + " ya fue despachado (" + d.getDispatchNumber()
                                + ") y no puede despacharse de nuevo.");
            }
        }
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

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserInfo userInfo) {
            return userInfo.userId();
        }
        return null;
    }

    private String generarNumeroDespacho() {
        String base = "DES-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        String numero = base;
        int intento = 1;
        while (dispatchRepository.existsByDispatchNumber(numero)) {
            numero = base + "-" + (intento++);
        }
        return numero;
    }

}