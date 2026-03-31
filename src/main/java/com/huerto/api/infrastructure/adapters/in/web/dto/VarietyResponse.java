package com.huerto.api.infrastructure.adapters.in.web.dto;

import com.huerto.api.domain.model.Variety;
import java.util.UUID;

public record VarietyResponse(
        UUID id,
        String name,
        String productCategory
) {
    public static VarietyResponse from(Variety variety) {
        return new VarietyResponse(
                variety.id(),
                variety.name(),
                variety.productCategory()
        );
    }
}