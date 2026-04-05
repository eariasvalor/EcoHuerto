package com.huerto.api.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterCustomerRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String rawPassword,

        @NotBlank(message = "Phone country code must not be blank")
        String phoneCountryCode,

        @NotBlank(message = "Phone number must not be blank")
        String phoneNumber
) {}