package com.huerto.api.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String rawPassword
) {}
