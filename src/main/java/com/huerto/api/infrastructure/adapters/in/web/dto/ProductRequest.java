package com.huerto.api.infrastructure.adapters.in.web.dto;
import com.huerto.api.domain.enums.Unit;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @NotNull(message = "Variety ID must not be null")
        UUID varietyId,

        @NotNull
        @DecimalMin(value = "0.0", message = "Price must be zero or positive")
        BigDecimal price,

        @NotNull(message = "Unit must not be null")
        Unit unit,

        @Min(value = 0, message = "Stock cannot be negative")
        int stock
) {}