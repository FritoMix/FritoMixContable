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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatchNotifierTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DispatchNotifier notifier;

    private Dispatch dispatch() {
        return Dispatch.builder().id(1L).dispatchNumber("DES-1").vehiclePlate("ABC123")
                .orders(List.of(Order.builder().id(1L).orderNumber("PED-1").build()))
                .build();
    }

    @Test
    void notifyCreatedConUsuarioYConductor() {
        User user = User.builder().id(5L).firstName("Ana").email("a@x.com").build();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        notifier.notifyCreated(dispatch(), List.of(Order.builder().id(1L).orderNumber("PED-1").build()),
                Driver.builder().id(1L).name("Juan").build(),
                Vehicle.builder().id(1L).vehicleNumber("ABC123").build(), 5L);

        verify(notificationService).createForRoles(anyString(), anyString(), eq("INFO"), anyString(), eq(RoleType.CARTERA), eq(RoleType.ADMIN));
        verify(notificationService).create(any(NotificationRequest.class));
        verify(emailService).sendEmailQuietly(eq("a@x.com"), startsWith("Nuevo despacho"), anyString());
    }

    @Test
    void notifyCreatedSinUsuarioNoEnviaEmail() {
        notifier.notifyCreated(dispatch(), List.of(Order.builder().id(1L).orderNumber("PED-1").build()),
                null, null, null);

        verify(emailService, never()).sendEmailQuietly(anyString(), anyString(), anyString());
        verify(notificationService, never()).create(any(NotificationRequest.class));
    }

    @Test
    void notifyCreatedConEmailEnBlancoNoEnvia() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(User.builder().id(5L).email("   ").build()));

        notifier.notifyCreated(dispatch(), List.of(Order.builder().build()), null, null, 5L);

        verify(emailService, never()).sendEmailQuietly(anyString(), anyString(), anyString());
    }

    @Test
    void notifyPlacaConfirmadaNotificaRolesYDespachador() {
        User despachador = User.builder().id(10L).firstName("Carlos").lastName("Ruiz").build();

        notifier.notifyPlacaConfirmada(dispatch(), despachador);

        verify(notificationService).createForRoles(anyString(), anyString(), eq("INFO"), anyString(), eq(RoleType.CARTERA), eq(RoleType.ADMIN));
        verify(notificationService).create(any(NotificationRequest.class));
    }

    @Test
    void notifyDispatchedNotifica() {
        notifier.notifyDispatched(dispatch());

        verify(notificationService).createForRoles(anyString(), anyString(), eq("SUCCESS"), anyString(), eq(RoleType.CARTERA), eq(RoleType.ADMIN));
    }

    @Test
    void notifyDispatchedNoPropagaExcepcion() {
        doThrow(new RuntimeException("boom")).when(notificationService)
                .createForRoles(anyString(), anyString(), anyString(), anyString(), any(RoleType.class), any(RoleType.class));

        notifier.notifyDispatched(dispatch());
    }
}