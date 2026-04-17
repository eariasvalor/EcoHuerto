package com.huerto.api.infrastructure.scheduler;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.model.Notification;
import com.huerto.api.domain.ports.out.NotificationRepository;
import com.huerto.api.domain.ports.out.WhatsAppPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "whatsapp.retry.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationRetryScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationRetryScheduler.class);
    private static final int MAX_ATTEMPTS = 3;

    private final NotificationRepository notificationRepository;
    private final WhatsAppPort whatsAppPort;

    public NotificationRetryScheduler(NotificationRepository notificationRepository,
                                      WhatsAppPort whatsAppPort) {
        this.notificationRepository = notificationRepository;
        this.whatsAppPort = whatsAppPort;
    }

    @Scheduled(fixedDelayString = "${whatsapp.retry.delay-ms:300000}")
    public void retryFailedNotifications() {
        List<Notification> failed = notificationRepository
                .findByDeliveryStatusAndAttemptsLessThan(DeliveryStatus.FAILED, MAX_ATTEMPTS);

        if (failed.isEmpty()) return;

        log.info("Retrying {} failed notifications", failed.size());

        for (Notification notification : failed) {
            retry(notification);
        }
    }

    private void retry(Notification notification) {
        try {
            whatsAppPort.sendManualNotification(
                    notification.recipientPhone(),
                    notification.messageText(),
                    notification.mediaId()
            );
            log.info("Retry succeeded for notification {}", notification.id());
        } catch (Exception e) {
            log.warn("Retry failed for notification {}: {}", notification.id(), e.getMessage());

            int newAttempts = notification.attempts() + 1;
            DeliveryStatus newStatus = newAttempts >= MAX_ATTEMPTS
                    ? DeliveryStatus.PERMANENTLY_FAILED
                    : DeliveryStatus.FAILED;

            notificationRepository.save(notification
                    .incrementAttempts()
                    .withDeliveryStatus(newStatus));
        }
    }
}