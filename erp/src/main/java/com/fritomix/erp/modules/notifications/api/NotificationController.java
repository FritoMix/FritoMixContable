package com.fritomix.erp.modules.notifications.api;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.modules.auth.application.dto.JwtUserInfo;
import com.fritomix.erp.modules.notifications.application.dto.response.NotificationResponse;
import com.fritomix.erp.modules.notifications.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_NOTIFICATIONS_VIEW')")
    public ResponseEntity<PageResponse<NotificationResponse>> findAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.findRecentByUserId(userId, pageable));
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
        if (principal instanceof JwtUserInfo userInfo) {
            return userInfo.userId();
        }
        throw new IllegalStateException("No se pudo obtener el ID del usuario autenticado");
    }
}
