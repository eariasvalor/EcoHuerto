package com.huerto.api.application.commands;

import java.util.UUID;

public record UpdateCustomerCommand(
        UUID customerId,
        String name,
        String rawPassword,
        String phoneCountryCode,
        String phoneNumber,
        String addressStreetType,
        String addressStreet,
        String addressNumber,
        String addressFloor,
        String addressCity,
        String addressPostalCode,
        String addressProvince
) {
    public UpdateCustomerCommand {
        if (customerId == null)
            throw new IllegalArgumentException("Customer ID must not be null");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name must not be blank");
        if (phoneCountryCode == null || phoneCountryCode.isBlank())
            throw new IllegalArgumentException("Phone country must not be blank");
        if (phoneNumber == null || phoneNumber.isBlank())
            throw  new IllegalArgumentException("Phone number must not be blank");
    }
}