package com.huerto.api.application.impl.notification;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.enums.NotificationType;
import com.huerto.api.domain.model.Notification;
import com.huerto.api.domain.ports.out.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetNotificationHistoryUseCaseImplTest {

    @Mock NotificationRepository notificationRepository;
    @InjectMocks GetNotificationHistoryUseCaseImpl useCase;

    @Test
    void should_return_paginated_notifications_by_status() {
        Notification notification = new Notification(
                UUID.randomUUID(), NotificationType.MANUAL, UUID.randomUUID(),
                "+34612345678", null, "Tomates disponibles!", null,
                DeliveryStatus.SENT, 0, LocalDateTime.now(), LocalDateTime.now()
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<Notification> page = new PageImpl<>(List.of(notification), pageable, 1);

        when(notificationRepository.findAll(DeliveryStatus.SENT, pageable)).thenReturn(page);

        Page<Notification> result = useCase.execute(DeliveryStatus.SENT, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).deliveryStatus()).isEqualTo(DeliveryStatus.SENT);
        verify(notificationRepository).findAll(DeliveryStatus.SENT, pageable);
    }
}