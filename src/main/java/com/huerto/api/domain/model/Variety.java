package com.huerto.api.domain.model;

import java.util.UUID;

public record Variety(
        UUID id,
        String name,
        String productCategory,
        String imageUrl
) {
    public Variety {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Variety name must not be blank");
        if (productCategory == null || productCategory.isBlank())
            throw new IllegalArgumentException("Product category must not be blank");
        name = name.trim();
        productCategory = productCategory.trim();
    }

    public Variety withImageUrl(String imageUrl) {
        return new Variety(id, name, productCategory, imageUrl);
    }
}
