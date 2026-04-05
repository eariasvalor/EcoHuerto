package com.huerto.api.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String rawPassword,

        @NotBlank(message = "Phone country code must not be blank")
        String phoneCountryCode,

        @NotBlank(message = "Phone number must not be blank")
        String phoneNumber,

        String addressStreetType,
        String addressStreet,
        String addressNumber,
        String addressFloor,
        String addressCity,
        String addressPostalCode,
        String addressProvince

) {}
