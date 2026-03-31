package com.huerto.api.domain.model;

import com.huerto.api.domain.valueobject.Price;

import java.util.UUID;

public record OrderLine(
        UUID id,
        Product product,
        int quantity
) {
    public OrderLine {
        if (product == null)
            throw new IllegalArgumentException("Product must not be null");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
    }


    public Price subtotal() {
        return product.price().multiply(quantity);
    }
}
