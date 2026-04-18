package com.huerto.api.application.usecase.notification;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetNotificationHistoryUseCase {
    Page<Notification> execute(DeliveryStatus status, Pageable pageable);
}