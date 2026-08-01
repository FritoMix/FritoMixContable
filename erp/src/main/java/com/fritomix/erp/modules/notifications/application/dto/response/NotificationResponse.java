package com.fritomix.erp.modules.notifications.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        Long id,
        String title,
        String message,
        String type,
        Boolean isRead,
        String link,
        LocalDateTime createdAt
) {
}
