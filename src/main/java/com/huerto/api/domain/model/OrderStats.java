package com.huerto.api.domain.model;

public record OrderStats(
        long pendingConfirmation,
        long confirmed,
        long inPreparation,
        long readyForPickup,
        long cancelled,
        long total
) {}