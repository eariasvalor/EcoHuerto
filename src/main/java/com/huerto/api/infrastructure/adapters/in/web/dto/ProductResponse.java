package com.huerto.api.infrastructure.adapters.in.web.dto;

import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.model.Product;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        String variety,
        String category,
        BigDecimal price,
        String currency,
        Unit unit,
        int stock,
        boolean available,
        String imageUrl
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.id(),
                product.name(),
                product.description().value(),
                product.variety().name(),
                product.variety().productCategory(),
                product.price().amount(),
                product.price().currency(),
                product.unit(),
                product.stock(),
                product.available(),
                product.imageUrl()
        );
    }
}