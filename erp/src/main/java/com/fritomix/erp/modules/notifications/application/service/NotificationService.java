package com.fritomix.erp.modules.notifications.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.enums.RoleType;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.notifications.application.dto.request.NotificationRequest;
import com.fritomix.erp.modules.notifications.application.dto.response.NotificationResponse;
import com.fritomix.erp.modules.notifications.domain.entity.Notification;
import com.fritomix.erp.modules.notifications.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.notifications.email-enabled:true}")
    private boolean emailEnabled;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> findRecentByUserId(Long userId, Pageable pageable) {
        return PageResponse.from(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable), this::toResponse);
    }

    @Transactional(readOnly = true)
    public long countUnreadByUserId(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void create(NotificationRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.userId()));
        Notification notification = Notification.builder()
                .user(user)
                .title(request.title())
                .message(request.message())
                .type(request.type() != null ? request.type() : "INFO")
                .link(request.link())
                .build();
        notificationRepository.save(notification);
        sendEmailNotification(user, request.title(), request.message(), request.link());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createForRoles(String title, String message, String type, String link, RoleType... roles) {
        for (RoleType role : roles) {
            List<User> users = userRepository.findByRoleName(role.name());
            for (User user : users) {
                Notification notification = Notification.builder()
                        .user(user)
                        .title(title)
                        .message(message)
                        .type(type != null ? type : "INFO")
                        .link(link)
                        .build();
                notificationRepository.save(notification);
                sendEmailNotification(user, title, message, link);
            }
        }
    }

    private void sendEmailNotification(User user, String title, String message, String link) {
        if (!emailEnabled || user.getEmail() == null || user.getEmail().isBlank()) {
            return;
        }
        String body = message;
        if (link != null && !link.isBlank()) {
            body += "\n\nVer más: " + frontendUrl + link;
        }
        emailService.sendEmailQuietly(user.getEmail(), "[FritoMix] " + title, body);
    }

    @Transactional
    public void markAsRead(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con id: " + id));

        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para modificar esta notificación");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(n -> !n.getIsRead())
                .collect(Collectors.toList());

        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .link(notification.getLink())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
