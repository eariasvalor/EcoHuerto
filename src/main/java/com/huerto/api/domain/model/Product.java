package com.huerto.api.domain.model;

import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.valueobject.Price;

import java.util.UUID;

public record Product(
        UUID id,
        String name,
        Variety variety,
        Price price,
        Unit unit,
        int stock,
        boolean available,
        int version
) {
    public Product {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Product name must not be blank");
        if (variety == null)
            throw new IllegalArgumentException("Variety must not be null");
        if (price == null)
            throw new IllegalArgumentException("Price must not be null");
        if (stock < 0)
            throw new IllegalArgumentException("Stock cannot be negative: " + stock);
        name = name.trim();
    }

    // --- Domain behaviour ---

    public boolean hasStock(int requested) {
        return this.stock >= requested;
    }

    public Product decreaseStock(int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        if (!hasStock(quantity))
            throw new IllegalStateException(
                    "Insufficient stock. Available: " + stock + ", requested: " + quantity);
        return new Product(id, name, variety, price, unit,
                stock - quantity, available, version);
    }

    public Product increaseStock(int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        return new Product(id, name, variety, price, unit,
                stock + quantity, available, version);
    }

    public Product toggleAvailability() {
        return new Product(id, name, variety, price, unit,
                stock, !available, version);
    }
}
