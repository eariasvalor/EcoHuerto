package com.huerto.api.application.commands;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID customerId,
        List<OrderLineCommand> lines
) {
    public record OrderLineCommand(UUID productId, int quantity) {
        public OrderLineCommand {
            if (productId == null)
                throw new IllegalArgumentException("Product ID must not be null");
            if (quantity <= 0)
                throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    public CreateOrderCommand {
        if (customerId == null)
            throw new IllegalArgumentException("Customer ID must not be null");
        if (lines == null || lines.isEmpty())
            throw new IllegalArgumentException("Order must have at least one line");
        lines = List.copyOf(lines);
    }
}