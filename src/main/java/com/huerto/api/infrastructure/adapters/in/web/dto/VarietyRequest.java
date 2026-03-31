package com.huerto.api.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record VarietyRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @NotBlank(message = "Product category must not be blank")
        String productCategory
) {}