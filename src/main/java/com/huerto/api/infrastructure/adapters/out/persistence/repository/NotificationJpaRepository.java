package com.huerto.api.infrastructure.adapters.out.persistence.repository;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByDeliveryStatusAndAttemptsLessThan(
            DeliveryStatus deliveryStatus, int maxAttempts);
    Page<NotificationEntity> findByDeliveryStatus(DeliveryStatus deliveryStatus, Pageable pageable);
}
