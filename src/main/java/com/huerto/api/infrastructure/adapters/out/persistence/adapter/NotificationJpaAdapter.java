package com.huerto.api.infrastructure.adapters.out.persistence.adapter;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.model.Notification;
import com.huerto.api.domain.ports.out.NotificationRepository;
import com.huerto.api.infrastructure.adapters.out.persistence.mapper.NotificationEntityMapper;
import com.huerto.api.infrastructure.adapters.out.persistence.repository.NotificationJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationJpaAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;
    private final NotificationEntityMapper mapper;

    public NotificationJpaAdapter(NotificationJpaRepository jpaRepository,
                                  NotificationEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Notification save(Notification notification) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(notification)));
    }

    @Override
    public List<Notification> findByDeliveryStatusAndAttemptsLessThan(
            DeliveryStatus status, int maxAttempts) {
        return jpaRepository
                .findByDeliveryStatusAndAttemptsLessThan(status, maxAttempts)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}