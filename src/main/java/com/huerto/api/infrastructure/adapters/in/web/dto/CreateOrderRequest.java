package com.huerto.api.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull(message = "Customer ID must not be null")
        UUID customerId,

        @NotEmpty(message = "Order must have at least one line")
        List<OrderLineRequest> lines
) {
    public record OrderLineRequest(
            @NotNull(message = "Product ID must not be null")
            UUID productId,

            @Positive(message = "Quantity must be positive")
            int quantity
    ) {}
}
