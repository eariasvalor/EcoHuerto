package com.huerto.api.application.commands;

public record CreateCustomerCommand(
        String name,
        String email,
        String rawPassword
) {}