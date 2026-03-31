package com.huerto.api.application.commands;

public record LoginCommand(
        String email,
        String rawPassword
) {
    public LoginCommand {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email must not be blank");
        if (rawPassword == null || rawPassword.isBlank())
            throw new IllegalArgumentException("Password must not be blank");
    }
}