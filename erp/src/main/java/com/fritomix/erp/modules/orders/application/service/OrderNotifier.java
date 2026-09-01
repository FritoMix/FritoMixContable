package com.fritomix.erp.modules.orders.application.service;

import com.fritomix.erp.modules.auth.domain.enums.RoleType;
import com.fritomix.erp.modules.customers.domain.entity.Customer;
import com.fritomix.erp.modules.notifications.application.dto.request.NotificationRequest;
import com.fritomix.erp.modules.notifications.application.service.EmailService;
import com.fritomix.erp.modules.notifications.application.service.NotificationService;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.push.application.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Centraliza las notificaciones, emails y push relacionados con pedidos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderNotifier {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final PushNotificationService pushNotificationService;

    public void notifyCreated(Order order, Customer customer, Long userId) {
        try {
            notificationService.createForRoles(
                    "Nuevo pedido",
                    "Se ha creado el pedido " + order.getOrderNumber() + " para el cliente " + customer.getBusinessName() + ".",
                    "INFO",
                    "/pedidos/" + order.getId(),
                    RoleType.CARTERA, RoleType.ADMIN
            );

            pushNotificationService.sendToRoles(
                    "Nuevo pedido - " + order.getOrderNumber(),
                    customer.getBusinessName() + " · está listo para despacho.",
                    "/pedidos/" + order.getId(),
                    RoleType.DESPACHADOR, RoleType.ADMIN
            );

            if (userId != null) {
                notificationService.create(NotificationRequest.builder()
                        .userId(userId)
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
    }

    public void notifyStatusChanged(Order order, String newStatus) {
        try {
            if (OrderStatusRules.STATUS_APROBADO.equals(newStatus)) {
                notificationService.createForRoles(
                        "Pedido aprobado",
                        "El pedido " + order.getOrderNumber() + " fue aprobado y está listo para despacho.",
                        "SUCCESS",
                        "/pedidos/" + order.getId(),
                        RoleType.DESPACHADOR, RoleType.ADMIN
                );
            } else if (OrderStatusRules.STATUS_CANCELADO.equals(newStatus)) {
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
    }
}