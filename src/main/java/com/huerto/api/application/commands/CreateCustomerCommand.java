package com.huerto.api.application.commands;

public record CreateCustomerCommand(
        String name,
        String email,
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
) {}