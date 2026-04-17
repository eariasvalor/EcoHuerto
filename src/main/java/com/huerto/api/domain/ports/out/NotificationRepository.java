package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.model.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository {
    Notification save(Notification notification);
    List<Notification> findByDeliveryStatusAndAttemptsLessThan(DeliveryStatus status, int maxAttempts);
}