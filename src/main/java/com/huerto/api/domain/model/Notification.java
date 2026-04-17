package com.huerto.api.domain.model;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record Notification (
        UUID id,
        NotificationType type,
        UUID customerId,
        String templateId,
        String messageText,
        String mediaId,
        DeliveryStatus deliveryStatus,
        int attempts,
        LocalDateTime createdAt,
        LocalDateTime sentAt
) {

    public Notification {
        if (type == null)
            throw new IllegalArgumentException("Type must not be null");
        if (customerId == null)
            throw new IllegalArgumentException("Customer ID must not be null");
        if (deliveryStatus == null)
            throw new IllegalArgumentException("Delivery status must not be null");
    }

    public Notification withDeliveryStatus(DeliveryStatus newStatus) {
        return new Notification(id, type, customerId, templateId, messageText,
                mediaId, newStatus, attempts, createdAt, sentAt);
    }

    public Notification withSentAt(LocalDateTime sentAt) {
        return new Notification(id, type, customerId, templateId, messageText,
                mediaId, deliveryStatus, attempts, createdAt, sentAt);
    }

    public Notification incrementAttempts() {
        return new Notification(id, type, customerId, templateId, messageText,
                mediaId, deliveryStatus, attempts + 1, createdAt, sentAt);
    }
}
