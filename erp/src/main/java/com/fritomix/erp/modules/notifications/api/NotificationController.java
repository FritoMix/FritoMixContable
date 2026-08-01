package com.fritomix.erp.modules.notifications.api;

import com.fritomix.erp.modules.notifications.application.dto.response.NotificationResponse;
import com.fritomix.erp.modules.notifications.application.service.NotificationService;
import com.fritomix.erp.security.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_NOTIFICATIONS_VIEW')")
    public ResponseEntity<List<NotificationResponse>> findAll() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.findRecentByUserId(userId));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAuthority('PERMISSION_NOTIFICATIONS_VIEW')")
    public ResponseEntity<Map<String, Long>> unreadCount() {
        Long userId = getCurrentUserId();
        long count = notificationService.countUnreadByUserId(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAuthority('PERMISSION_NOTIFICATIONS_MARK_READ')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        notificationService.markAsRead(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    @PreAuthorize("hasAuthority('PERMISSION_NOTIFICATIONS_MARK_READ')")
    public ResponseEntity<Void> markAllAsRead() {
        Long userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser().getId();
        }
        throw new IllegalStateException("No se pudo obtener el ID del usuario autenticado");
    }
}
