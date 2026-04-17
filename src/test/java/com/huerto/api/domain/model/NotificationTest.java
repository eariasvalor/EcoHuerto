package com.huerto.api.domain.model;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.enums.NotificationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class NotificationTest {

    private Notification buildNotification() {
        return new Notification(
                UUID.randomUUID(),
                NotificationType.STATUS_CHANGE,
                UUID.randomUUID(),
                "+34612345678",
                "template_id",
                "message text",
                null,
                DeliveryStatus.PENDING,
                0,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void should_update_delivery_status() {
        Notification notification = buildNotification();
        Notification updated = notification.withDeliveryStatus(DeliveryStatus.SENT);

        assertThat(updated.deliveryStatus()).isEqualTo(DeliveryStatus.SENT);
        assertThat(notification.deliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
    }

    @Test
    void should_increment_attempts() {
        Notification notification = buildNotification();
        Notification updated = notification.incrementAttempts();

        assertThat(updated.attempts()).isEqualTo(1);
        assertThat(notification.attempts()).isEqualTo(0);
    }

    @Test
    void should_set_sent_at() {
        Notification notification = buildNotification();
        LocalDateTime sentAt = LocalDateTime.now();
        Notification updated = notification.withSentAt(sentAt);

        assertThat(updated.sentAt()).isEqualTo(sentAt);
        assertThat(notification.sentAt()).isNull();
    }

    @Test
    void should_throw_when_type_is_null() {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        assertThatThrownBy(() -> new Notification(
                id, null, customerId, "+34612345678",
                null, null, null, DeliveryStatus.PENDING, 0, now, null
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_customer_id_is_null() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        assertThatThrownBy(() -> new Notification(
                id, NotificationType.STATUS_CHANGE, null, "+34612345678",
                null, null, null, DeliveryStatus.PENDING, 0, now, null
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_delivery_status_is_null() {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        assertThatThrownBy(() -> new Notification(
                id, NotificationType.STATUS_CHANGE, customerId, "+34612345678",
                null, null, null, null, 0, now, null
        )).isInstanceOf(IllegalArgumentException.class);
    }
}