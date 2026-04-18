package com.huerto.api.infrastructure.adapters.in.web.dto;

import java.util.List;
import java.util.UUID;

public record SendManualNotificationRequest(
        List<UUID> customerIds,
        String messageText
) {}