package com.huerto.api.application.impl.notification;

import com.huerto.api.application.usecase.notification.GetNotificationHistoryUseCase;
import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.model.Notification;
import com.huerto.api.domain.ports.out.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetNotificationHistoryUseCaseImpl implements GetNotificationHistoryUseCase {

    private final NotificationRepository notificationRepository;

    public GetNotificationHistoryUseCaseImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Page<Notification> execute(DeliveryStatus status, Pageable pageable) {
        return notificationRepository.findAll(status, pageable);
    }
}