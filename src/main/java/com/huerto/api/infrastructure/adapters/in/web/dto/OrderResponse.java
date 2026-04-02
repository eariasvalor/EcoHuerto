package com.huerto.api.infrastructure.adapters.in.web.dto;

import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String visibleId,
        UUID customerId,
        String customerName,
        List<OrderLineResponse> lines,
        OrderStatus status,
        BigDecimal total,
        LocalDateTime createdAt,
        boolean possibleDuplicate
) {
    public record OrderLineResponse(
            UUID id,
            UUID productId,
            String productName,
            String productVariety,
            BigDecimal productPrice,
            String productUnit,
            int quantity,
            BigDecimal subtotal
    ) {}

    public static OrderResponse from(Order order, boolean possibleDuplicate) {
        List<OrderLineResponse> lines = order.lines().stream()
                .map(line -> new OrderLineResponse(
                        line.id(),
                        line.product().id(),
                        line.product().name(),
                        line.product().variety().name(),
                        line.product().price().amount(),
                        line.product().unit().name(),
                        line.quantity(),
                        line.subtotal().amount()
                ))
                .toList();

        return new OrderResponse(
                order.id(),
                order.visibleId(),
                order.customerId(),
                order.customerName(),
                lines,
                order.status(),
                order.total().amount(),
                order.createdAt(),
                possibleDuplicate
        );
    }

    public static OrderResponse from(Order order) {
        return from(order, false);
    }
}