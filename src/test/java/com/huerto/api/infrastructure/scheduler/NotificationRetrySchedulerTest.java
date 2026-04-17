package com.huerto.api.infrastructure.scheduler;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.enums.NotificationType;
import com.huerto.api.domain.model.Notification;
import com.huerto.api.domain.ports.out.NotificationRepository;
import com.huerto.api.domain.ports.out.WhatsAppPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationRetrySchedulerTest {

    @Mock NotificationRepository notificationRepository;
    @Mock WhatsAppPort whatsAppPort;
    @InjectMocks NotificationRetryScheduler scheduler;

    private Notification buildFailedNotification(int attempts) {
        return new Notification(
                UUID.randomUUID(),
                NotificationType.STATUS_CHANGE,
                UUID.randomUUID(),
                "+34612345678",
                "order_status_change",
                "Tu pedido ha cambiado de estado",
                null,
                DeliveryStatus.FAILED,
                attempts,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void should_retry_failed_notifications_and_not_update_on_success() {
        Notification failed = buildFailedNotification(1);

        when(notificationRepository.findByDeliveryStatusAndAttemptsLessThan(
                DeliveryStatus.FAILED, 3)).thenReturn(List.of(failed));

        scheduler.retryFailedNotifications();

        verify(whatsAppPort).sendManualNotification(
                "+34612345678",
                "Tu pedido ha cambiado de estado",
                null
        );
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void should_mark_as_permanently_failed_when_max_attempts_reached() {
        Notification failed = buildFailedNotification(2);

        when(notificationRepository.findByDeliveryStatusAndAttemptsLessThan(
                DeliveryStatus.FAILED, 3)).thenReturn(List.of(failed));

        doThrow(new RuntimeException("Meta API error"))
                .when(whatsAppPort).sendManualNotification(any(), any(), any());

        scheduler.retryFailedNotifications();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        assertThat(captor.getValue().deliveryStatus())
                .isEqualTo(DeliveryStatus.PERMANENTLY_FAILED);
        assertThat(captor.getValue().attempts()).isEqualTo(3);
    }

    @Test
    void should_keep_as_failed_when_retry_fails_but_attempts_below_max() {
        Notification failed = buildFailedNotification(1);

        when(notificationRepository.findByDeliveryStatusAndAttemptsLessThan(
                DeliveryStatus.FAILED, 3)).thenReturn(List.of(failed));

        doThrow(new RuntimeException("Meta API error"))
                .when(whatsAppPort).sendManualNotification(any(), any(), any());

        scheduler.retryFailedNotifications();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        assertThat(captor.getValue().deliveryStatus()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(captor.getValue().attempts()).isEqualTo(2);
    }

    @Test
    void should_do_nothing_when_no_failed_notifications() {
        when(notificationRepository.findByDeliveryStatusAndAttemptsLessThan(
                DeliveryStatus.FAILED, 3)).thenReturn(List.of());

        scheduler.retryFailedNotifications();

        verify(whatsAppPort, never()).sendManualNotification(any(), any(), any());
        verify(notificationRepository, never()).save(any());
    }
}