package com.huerto.api.domain.events;

import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.Order;

import java.time.LocalDateTime;

public record OrderStatusChangedEvent(
        Order order,
        OrderStatus previousStatus,
        OrderStatus newStatus,
        LocalDateTime occurredAt
) {}
