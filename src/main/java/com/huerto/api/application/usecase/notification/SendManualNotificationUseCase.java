package com.huerto.api.application.usecase.notification;

import com.huerto.api.infrastructure.adapters.in.web.dto.SendManualNotificationResponse;

import java.util.List;
import java.util.UUID;

public interface SendManualNotificationUseCase {
    SendManualNotificationResponse execute(List<UUID> customerIds, String messageText, String mediaUrl);
}