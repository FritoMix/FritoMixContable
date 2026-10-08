package com.fritomix.erp.modules.orders.application.service;

import com.fritomix.erp.modules.auth.domain.enums.RoleType;
import com.fritomix.erp.modules.customers.domain.entity.Customer;
import com.fritomix.erp.modules.notifications.application.dto.request.NotificationRequest;
import com.fritomix.erp.modules.notifications.application.service.EmailService;
import com.fritomix.erp.modules.notifications.application.service.NotificationService;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.push.application.service.PushNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderNotifierTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private PushNotificationService pushNotificationService;

    @InjectMocks
    private OrderNotifier notifier;

    private Order order() {
        return Order.builder().id(1L).orderNumber("PED-1").build();
    }

    private Customer customer(String email) {
        return Customer.builder().id(1L).businessName("Cliente X").email(email).build();
    }

    @Test
    void notifyCreatedConEmailYUsuario() {
        notifier.notifyCreated(order(), customer("c@x.com"), 5L);

        verify(notificationService).createForRoles(anyString(), anyString(), anyString(), anyString(), eq(RoleType.CARTERA), eq(RoleType.ADMIN));
        verify(pushNotificationService).sendToRoles(anyString(), anyString(), anyString(), eq(RoleType.CARTERA), eq(RoleType.ADMIN));
        verify(notificationService).create(any(NotificationRequest.class));
        verify(emailService).sendEmailQuietly(eq("c@x.com"), startsWith("Nuevo pedido"), anyString());
    }

    @Test
    void notifyCreatedSinEmailNiUsuario() {
        notifier.notifyCreated(order(), customer(null), null);

        verify(emailService, never()).sendEmailQuietly(anyString(), anyString(), anyString());
        verify(notificationService, never()).create(any(NotificationRequest.class));
    }

    @Test
    void notifyCreatedConEmailEnBlancoNoEnvia() {
        notifier.notifyCreated(order(), customer("   "), null);

        verify(emailService, never()).sendEmailQuietly(anyString(), anyString(), anyString());
    }

    @Test
    void notifyStatusChangedAprobadoNotificaProduccion() {
        notifier.notifyStatusChanged(order(), OrderStatusRules.STATUS_APROBADO);

        verify(notificationService).createForRoles(anyString(), anyString(), eq("SUCCESS"), anyString(), eq(RoleType.PRODUCCION), eq(RoleType.ADMIN));
    }

    @Test
    void notifyStatusChangedEnProduccionNotifica() {
        notifier.notifyStatusChanged(order(), OrderStatusRules.STATUS_EN_PRODUCCION);

        verify(notificationService).createForRoles(anyString(), anyString(), eq("INFO"), anyString(), eq(RoleType.ADMIN));
    }

    @Test
    void notifyStatusChangedListoProduccionNotificaDespachador() {
        notifier.notifyStatusChanged(order(), OrderStatusRules.STATUS_LISTO_PRODUCCION);

        verify(notificationService).createForRoles(anyString(), anyString(), eq("SUCCESS"), anyString(), eq(RoleType.DESPACHADOR), eq(RoleType.ADMIN));
    }

    @Test
    void notifyStatusChangedCanceladoNotifica() {
        notifier.notifyStatusChanged(order(), OrderStatusRules.STATUS_CANCELADO);

        verify(notificationService).createForRoles(anyString(), anyString(), eq("WARNING"), anyString(), eq(RoleType.ADMIN));
    }

    @Test
    void notifyStatusChangedOtroEstadoNoNotifica() {
        notifier.notifyStatusChanged(order(), "OTRO");

        verify(notificationService, never()).createForRoles(anyString(), anyString(), anyString(), anyString(), any(RoleType.class));
    }

    @Test
    void notifyStatusChangedNoPropagaExcepcion() {
        doThrow(new RuntimeException("boom")).when(notificationService)
                .createForRoles(anyString(), anyString(), anyString(), anyString(), any(RoleType.class), any(RoleType.class));

        notifier.notifyStatusChanged(order(), OrderStatusRules.STATUS_APROBADO);
    }
}