package com.huerto.api.domain.model;

import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.valueobject.Price;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Order(
        UUID id,
        String visibleId,
        UUID customerId,
        List<OrderLine> lines,
        OrderStatus status,
        LocalDateTime createdAt,
        int version
) {
    public Order {
        if (visibleId == null || visibleId.isBlank())
            throw new IllegalArgumentException("Visible ID must not be blank");
        if (customerId == null)
            throw new IllegalArgumentException("Customer ID must not be null");
        if (lines == null || lines.isEmpty())
            throw new IllegalArgumentException("Order must have at least one line");
        if (status == null)
            throw new IllegalArgumentException("Status must not be null");
        lines = List.copyOf(lines);
    }


    public Price total() {
        return lines.stream()
                .map(OrderLine::subtotal)
                .reduce(Price.ZERO, Price::add);
    }


    public Order confirm() {
        assertTransition(OrderStatus.PENDING_CONFIRMATION, OrderStatus.CONFIRMED);
        return withStatus(OrderStatus.CONFIRMED);
    }

    public Order startPreparation() {
        assertTransition(OrderStatus.CONFIRMED, OrderStatus.IN_PREPARATION);
        return withStatus(OrderStatus.IN_PREPARATION);
    }

    public Order markReady() {
        assertTransition(OrderStatus.IN_PREPARATION, OrderStatus.READY_FOR_PICKUP);
        return withStatus(OrderStatus.READY_FOR_PICKUP);
    }

    public Order cancel() {
        if (this.status == OrderStatus.CANCELLED)
            throw new InvalidStatusTransitionException(status, OrderStatus.CANCELLED);
        return withStatus(OrderStatus.CANCELLED);
    }

    public boolean isActive() {
        return this.status != OrderStatus.CANCELLED;
    }


    private Order withStatus(OrderStatus newStatus) {
        return new Order(id, visibleId, customerId, lines,
                newStatus, createdAt, version);
    }

    private void assertTransition(OrderStatus expected, OrderStatus next) {
        if (this.status != expected)
            throw new InvalidStatusTransitionException(this.status, next);
    }
}
