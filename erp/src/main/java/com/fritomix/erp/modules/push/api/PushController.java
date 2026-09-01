package com.fritomix.erp.modules.push.api;

import com.fritomix.erp.modules.auth.application.dto.JwtUserInfo;
import com.fritomix.erp.modules.push.application.dto.request.PushSubscriptionRequest;
import com.fritomix.erp.modules.push.application.service.PushNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/push")
@RequiredArgsConstructor
public class PushController {

    private final PushNotificationService pushNotificationService;

    @PostMapping("/subscribe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> subscribe(@Valid @RequestBody PushSubscriptionRequest request) {
        pushNotificationService.subscribe(getCurrentUserId(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/subscribe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unsubscribe(@RequestParam String endpoint) {
        pushNotificationService.unsubscribe(getCurrentUserId(), endpoint);
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
