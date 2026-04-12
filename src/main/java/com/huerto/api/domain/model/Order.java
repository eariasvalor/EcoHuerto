package com.huerto.api.domain.model;

import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.exception.InvalidStatusTransitionException;
import com.huerto.api.domain.valueobject.Price;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Order(
        UUID id,
        String visibleId,
        UUID customerId,
        String customerName,
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
        customerName = customerName != null ? customerName : "";
        lines = List.copyOf(lines);
    }

    public Price total() {
        return lines.stream()
                .map(OrderLine::subtotal)
                .reduce(Price.ZERO, Price::add);
    }

    public Order confirm() {
        assertTransition(OrderStatus.CONFIRMED);
        return withStatus(OrderStatus.CONFIRMED);
    }

    public Order markReady() {
        assertTransition(OrderStatus.READY_FOR_PICKUP);
        return withStatus(OrderStatus.READY_FOR_PICKUP);
    }

    public Order deliver() {
        assertTransition(OrderStatus.DELIVERED);
        return withStatus(OrderStatus.DELIVERED);
    }

    public Order cancel() {
        assertTransition(OrderStatus.CANCELLED);
        return withStatus(OrderStatus.CANCELLED);
    }

    public Order revert() {
        if (this.status != OrderStatus.CONFIRMED)
            throw new InvalidStatusTransitionException(this.status, OrderStatus.PENDING);
        return withStatus(OrderStatus.PENDING);
    }

    public boolean isActive() {
        return this.status != OrderStatus.CANCELLED
                && this.status != OrderStatus.DELIVERED;
    }

    private Order withStatus(OrderStatus newStatus) {
        return new Order(id, visibleId, customerId, customerName, lines,
                newStatus, createdAt, version);
    }

    private void assertTransition(OrderStatus target) {
        if (!this.status.canTransitionTo(target))
            throw new InvalidStatusTransitionException(this.status, target);
    }
}