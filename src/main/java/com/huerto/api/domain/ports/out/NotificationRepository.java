package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository {
    Notification save(Notification notification);
    List<Notification> findByDeliveryStatusAndAttemptsLessThan(DeliveryStatus status, int maxAttempts);
    Page<Notification> findAll(DeliveryStatus status, Pageable pageable);
}