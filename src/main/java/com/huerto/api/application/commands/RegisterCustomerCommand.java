package com.huerto.api.application.commands;

public record RegisterCustomerCommand(
        String name,
        String email,
        String rawPassword,
        String phoneCountryCode,
        String phoneNumber
) {
    public RegisterCustomerCommand {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name must not be blank");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email must not be blank");
        if (rawPassword == null || rawPassword.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");
        if (phoneCountryCode == null || phoneCountryCode.isBlank())
            throw new IllegalArgumentException("Phone country must not be blank");
        if (phoneNumber == null || phoneNumber.isBlank())
            throw  new IllegalArgumentException("Phone number must not be blank");
    }
}