package com.fritomix.erp.modules.dispatch.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.dispatch.application.dto.request.DispatchRequest;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse;
import com.fritomix.erp.modules.dispatch.application.mapper.DispatchMapper;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DispatchService {

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

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + request.orderId()));
        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id: " + request.driverId()));
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con id: " + request.vehicleId()));

        Dispatch dispatch = Dispatch.builder()
                .order(order)
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
                Product product = productRepository.findById(dto.productId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + dto.productId()));
                DispatchDetail detail = DispatchDetail.builder()
                        .dispatch(dispatch)
                        .product(product)
                        .quantity(dto.quantity())
                        .delivered(dto.delivered() != null ? dto.delivered() : BigDecimal.ZERO)
                        .observations(dto.observations())
                        .build();
                dispatch.getDetails().add(detail);
            }
        }

        dispatch = dispatchRepository.save(dispatch);

        try {
            if (request.userId() != null) {
                notificationService.create(NotificationRequest.builder()
                        .userId(request.userId())
                        .title("Despacho creado")
                        .message("El despacho " + dispatch.getDispatchNumber() + " fue creado exitosamente.")
                        .type("SUCCESS")
                        .link("/despachos/" + dispatch.getId())
                        .build());

                User dispatchUser = userRepository.findById(request.userId()).orElse(null);
                if (dispatchUser != null && dispatchUser.getEmail() != null && !dispatchUser.getEmail().isBlank()) {
                    emailService.sendEmail(
                            dispatchUser.getEmail(),
                            "Nuevo despacho - FritoMix",
                            "Hola " + dispatchUser.getFirstName() + ",\n\n"
                                    + "Se ha creado un nuevo despacho: " + dispatch.getDispatchNumber() + ".\n"
                                    + "Conductor: " + driver.getName() + "\n"
                                    + "Vehículo: " + vehicle.getPlate() + "\n\n"
                                    + "FritoMix S.A.S."
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error al crear notificación/email para despacho {}: {}", dispatch.getDispatchNumber(), e.getMessage(), e);
        }

        return mapper.toResponse(dispatch);
    }

    @Transactional
    public DispatchResponse update(Long id, DispatchRequest request) {
        Dispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));

        if (request.orderId() != null && !request.orderId().equals(dispatch.getOrder().getId())) {
            Order order = orderRepository.findById(request.orderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + request.orderId()));
            dispatch.setOrder(order);
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
                Product product = productRepository.findById(dto.productId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + dto.productId()));
                DispatchDetail detail = DispatchDetail.builder()
                        .dispatch(dispatch)
                        .product(product)
                        .quantity(dto.quantity())
                        .delivered(dto.delivered() != null ? dto.delivered() : BigDecimal.ZERO)
                        .observations(dto.observations())
                        .build();
                dispatch.getDetails().add(detail);
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
        dispatch.setStatus(status);
        dispatch = dispatchRepository.save(dispatch);

        return mapper.toResponse(dispatch);
    }

}
