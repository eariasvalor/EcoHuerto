package com.huerto.api.infrastructure.adapters.out.persistence.mapper;

import com.huerto.api.domain.model.Notification;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationEntityMapper {

    public NotificationEntity toEntity(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(notification.id());
        entity.setType(notification.type());
        entity.setCustomerId(notification.customerId());
        entity.setRecipientPhone(notification.recipientPhone());
        entity.setTemplateId(notification.templateId());
        entity.setMessageText(notification.messageText());
        entity.setMediaId(notification.mediaId());
        entity.setDeliveryStatus(notification.deliveryStatus());
        entity.setAttempts(notification.attempts());
        entity.setCreatedAt(notification.createdAt());
        entity.setSentAt(notification.sentAt());
        return entity;
    }

    public Notification toDomain(NotificationEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getType(),
                entity.getCustomerId(),
                entity.getRecipientPhone(),
                entity.getTemplateId(),
                entity.getMessageText(),
                entity.getMediaId(),
                entity.getDeliveryStatus(),
                entity.getAttempts(),
                entity.getCreatedAt(),
                entity.getSentAt()
        );
    }
}