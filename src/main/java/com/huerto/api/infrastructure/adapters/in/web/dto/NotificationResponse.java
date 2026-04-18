package com.huerto.api.infrastructure.adapters.in.web.dto;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.enums.NotificationType;
import com.huerto.api.domain.model.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        NotificationType type,
        UUID customerId,
        String messageExcerpt,
        DeliveryStatus deliveryStatus,
        int attempts,
        LocalDateTime createdAt,
        LocalDateTime sentAt
) {
    private static final int EXCERPT_LENGTH = 80;

    public static NotificationResponse from(Notification notification) {
        String excerpt = notification.messageText() != null && notification.messageText().length() > EXCERPT_LENGTH
                ? notification.messageText().substring(0, EXCERPT_LENGTH) + "..."
                : notification.messageText();

        return new NotificationResponse(
                notification.id(),
                notification.type(),
                notification.customerId(),
                excerpt,
                notification.deliveryStatus(),
                notification.attempts(),
                notification.createdAt(),
                notification.sentAt()
        );
    }
}