package com.huerto.api.domain.events;

import com.huerto.api.domain.model.Order;

import java.time.LocalDateTime;

public record OrderCreatedEvent(
        Order order,
        LocalDateTime occurredAt
) {}