package com.huerto.api.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotNull;

public record StockRequest(
        @NotNull(message = "Quantity must not be null")
        Integer quantity
) {}
