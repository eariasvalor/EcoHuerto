package com.huerto.api.application.commands;

import com.huerto.api.domain.enums.Unit;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductCommand(
        UUID id,
        String name,
        String description,
        UUID varietyId,
        BigDecimal price,
        Unit unit
) {
    public UpdateProductCommand {
        if (id == null)
            throw new IllegalArgumentException("Product ID must not be null");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name must not be blank");
        if (description == null || description.isBlank())
            throw  new IllegalArgumentException("Description must not be blank");
        if (varietyId == null)
            throw new IllegalArgumentException("Variety ID must not be null");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be zero or positive");
    }
}