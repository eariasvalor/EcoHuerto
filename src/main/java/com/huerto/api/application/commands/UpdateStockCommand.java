package com.huerto.api.application.commands;

import java.util.UUID;

public record UpdateStockCommand(
        UUID productId,
        int quantity
) {
    public UpdateStockCommand {
        if (productId == null)
            throw new IllegalArgumentException("Product ID must not be null");
        if (quantity == 0)
            throw new IllegalArgumentException("Quantity must not be zero");
    }
}
