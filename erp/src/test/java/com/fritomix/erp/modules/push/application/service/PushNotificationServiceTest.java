package com.fritomix.erp.modules.push.application.service;

import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.enums.RoleType;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.push.application.dto.request.PushSubscriptionRequest;
import com.fritomix.erp.modules.push.domain.entity.PushSubscription;
import com.fritomix.erp.modules.push.domain.repository.PushSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PushNotificationServiceTest {

    @Mock
    private PushSubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @InjectMocks
    private PushNotificationService service;

    private final PushSubscriptionRequest request =
            new PushSubscriptionRequest("https://example.push/endpoint", "p256dhKey", "authKey");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "pushEnabled", false);
        ReflectionTestUtils.setField(service, "vapidPublicKey", "pub");
        ReflectionTestUtils.setField(service, "vapidPrivateKey", "priv");
        ReflectionTestUtils.setField(service, "vapidSubject", "mailto:test@fritomix.com");
    }

    @Test
    void subscribe_upserts_existingSubscription() {
        User user = new User();
        user.setId(7L);
        PushSubscription existing = PushSubscription.builder().id(1L).user(user)
                .endpoint(request.endpoint()).p256dh("old").auth("old").build();

        when(subscriptionRepository.findByUserIdAndEndpoint(7L, request.endpoint())).thenReturn(Optional.of(existing));
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));

        service.subscribe(7L, request);

        verify(subscriptionRepository).delete(existing);
        verify(subscriptionRepository).flush();
        ArgumentCaptor<PushSubscription> captor = ArgumentCaptor.forClass(PushSubscription.class);
        verify(subscriptionRepository).save(captor.capture());
        assertEquals(request.p256dh(), captor.getValue().getP256dh());
        assertEquals("authKey", captor.getValue().getAuth());
    }

    @Test
    void subscribe_savesWhenNoExisting() {
        User user = new User();
        user.setId(7L);
        when(subscriptionRepository.findByUserIdAndEndpoint(7L, request.endpoint())).thenReturn(Optional.empty());
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));

        service.subscribe(7L, request);

        verify(subscriptionRepository, never()).delete(any());
        verify(subscriptionRepository, never()).flush();
        verify(subscriptionRepository).save(any());
    }

    @Test
    void unsubscribe_delegates() {
        service.unsubscribe(7L, "https://example.push/endpoint");
        verify(subscriptionRepository).deleteByUserIdAndEndpoint(7L, "https://example.push/endpoint");
    }

    @Test
    void sendToRoles_doesNothing_whenPushDisabled() {
        service.sendToRoles("Título", "Cuerpo", "/pedidos/1", RoleType.DESPACHADOR, RoleType.ADMIN);
        verify(subscriptionRepository, never()).findByUserId(anyLong());
        verify(subscriptionRepository, never()).delete(any());
    }
}
