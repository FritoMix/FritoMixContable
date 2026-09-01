package com.fritomix.erp.modules.push.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.enums.RoleType;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.push.application.dto.request.PushSubscriptionRequest;
import com.fritomix.erp.modules.push.domain.entity.PushSubscription;
import com.fritomix.erp.modules.push.domain.repository.PushSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationService {

    private final PushSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.push.vapid.public-key:}")
    private String vapidPublicKey;

    @Value("${app.push.vapid.private-key:}")
    private String vapidPrivateKey;

    @Value("${app.push.vapid.subject:mailto:soporte@fritomix.com}")
    private String vapidSubject;

    @Value("${app.push.enabled:false}")
    private boolean pushEnabled;

    @Transactional
    public void subscribe(Long userId, PushSubscriptionRequest request) {
        // Upsert por (usuario, endpoint): elimina lo existente y vuelve a insertar.
        subscriptionRepository.findByUserIdAndEndpoint(userId, request.endpoint()).ifPresent(existing -> {
            subscriptionRepository.delete(existing);
            subscriptionRepository.flush();
        });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado con id: " + userId));

        subscriptionRepository.save(PushSubscription.builder()
                .user(user)
                .endpoint(request.endpoint())
                .p256dh(request.p256dh())
                .auth(request.auth())
                .build());
    }

    @Transactional
    public void unsubscribe(Long userId, String endpoint) {
        subscriptionRepository.deleteByUserIdAndEndpoint(userId, endpoint);
    }

    /**
     * Envía una notificación push a todos los usuarios con alguno de los roles indicados.
     * Nunca lanza excepciones: si un endpoint falla (410 Gone), se elimina la suscripción.
     */
    public void sendToRoles(String title, String body, String url, RoleType... roles) {
        if (!pushEnabled) {
            log.debug("Push deshabilitado, omitiendo envío de '{}'", title);
            return;
        }

        Set<Long> userIds = new HashSet<>();
        for (RoleType role : roles) {
            userRepository.findByRoleName(role.name()).forEach(u -> userIds.add(u.getId()));
        }

        for (Long userId : userIds) {
            List<PushSubscription> subscriptions = subscriptionRepository.findByUserId(userId);
            for (PushSubscription subscription : subscriptions) {
                sendToSubscription(subscription, title, body, url);
            }
        }
    }

    private void sendToSubscription(PushSubscription subscription, String title, String body, String url) {
        try {
            PushService pushService = createPushService();
            String payload = buildPayload(title, body, url);
            Notification notification = new Notification(subscription.getEndpoint(), subscription.getP256dh(), subscription.getAuth(), payload);
            HttpResponse response = pushService.send(notification);
            if (response.getStatusLine().getStatusCode() == 410) {
                subscriptionRepository.delete(subscription);
                log.info("Suscripción push eliminada (410 Gone): {}", subscription.getEndpoint());
            }
        } catch (Exception e) {
            log.warn("Error enviando push a {}: {}", subscription.getEndpoint(), e.getMessage());
        }
    }

    private PushService createPushService() throws Exception {
        return new PushService(vapidPublicKey, vapidPrivateKey, vapidSubject);
    }

    private String buildPayload(String title, String body, String url) {
        ObjectNode root = objectMapper.createObjectNode();
        ObjectNode notification = root.putObject("notification");
        notification.put("title", title);
        notification.put("body", body);
        notification.put("icon", "pwa-icons/icon-192.png");
        notification.put("badge", "pwa-icons/badge.png");
        notification.put("timestamp", System.currentTimeMillis());

        ObjectNode onActionClick = notification.putObject("data").putObject("onActionClick");
        ObjectNode defaultAction = onActionClick.putObject("default");
        defaultAction.put("operation", "navigateLastFocusedOrOpen");
        defaultAction.put("url", url != null ? url : "/");

        try {
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            log.error("Error serializando payload push: {}", e.getMessage());
            throw new IllegalStateException("No se pudo construir el payload push", e);
        }
    }
}
