package com.fritomix.erp.modules.notifications.application.dto.request;

import lombok.Builder;

@Builder
public record NotificationRequest(
        Long userId,
        String title,
        String message,
        String type,
        String link
) {}
