package com.huerto.api.application.commands;

import com.huerto.api.domain.enums.Unit;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductCommand(
        String name,
        UUID varietyId,
        BigDecimal price,
        Unit unit,
        int stock
) {
    public CreateProductCommand {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name must not be blank");
        if (varietyId == null)
            throw new IllegalArgumentException("Variety ID must not be null");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be zero or positive");
        if (stock < 0)
            throw new IllegalArgumentException("Stock cannot be negative");
    }
}