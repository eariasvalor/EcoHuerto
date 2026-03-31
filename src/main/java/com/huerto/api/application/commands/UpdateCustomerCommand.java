package com.huerto.api.application.commands;

import java.util.UUID;

public record UpdateCustomerCommand(
        UUID customerId,
        String name,
        String rawPassword
) {
    public UpdateCustomerCommand {
        if (customerId == null)
            throw new IllegalArgumentException("Customer ID must not be null");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name must not be blank");
    }
}