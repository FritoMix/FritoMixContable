package com.fritomix.erp.modules.dispatch.application.service;

import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.enums.RoleType;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.notifications.application.dto.request.NotificationRequest;
import com.fritomix.erp.modules.notifications.application.service.EmailService;
import com.fritomix.erp.modules.notifications.application.service.NotificationService;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Centraliza las notificaciones y emails relacionados con despachos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DispatchNotifier {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public void notifyCreated(Dispatch dispatch, List<Order> orders, Driver driver, Vehicle vehicle, Long userId) {
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
                    emailService.sendEmailQuietly(
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

    public void notifyDispatched(Dispatch dispatch) {
        String orderNumbers = dispatch.getOrders().stream()
                .map(Order::getOrderNumber)
                .collect(Collectors.joining(", "));
        try {
            notificationService.createForRoles(
                    "Despacho despachado",
                    "El despacho " + dispatch.getDispatchNumber() + " fue despachado (pedidos: " + orderNumbers + ").",
                    "SUCCESS",
                    "/despachos/" + dispatch.getId(),
                    RoleType.CARTERA, RoleType.ADMIN
            );
        } catch (Exception e) {
            log.error("Error al notificar cambio de estado del despacho {}: {}", dispatch.getDispatchNumber(), e.getMessage(), e);
        }
    }
}