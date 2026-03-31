package com.huerto.api.domain.valueobject;

import com.huerto.api.domain.ports.out.PasswordHasher;

import java.util.Objects;

public record Credentials(Email email, String passwordHash) {
    public Credentials {
        Objects.requireNonNull(email,        "Email must not be null");
        Objects.requireNonNull(passwordHash, "Password hash must not be null");
        if (passwordHash.isBlank())
            throw new IllegalArgumentException("Password hash must not be blank");
    }


    public static Credentials create(Email email,
                                     String rawPassword,
                                     PasswordHasher hasher) {
        Objects.requireNonNull(rawPassword, "Password must not be null");
        if (rawPassword.length() < 8)
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long");
        return new Credentials(email, hasher.hash(rawPassword));
    }


    public boolean authenticate(String rawPassword, PasswordHasher hasher) {
        if (rawPassword == null || rawPassword.isBlank()) return false;
        return hasher.verify(rawPassword, this.passwordHash);
    }


    @Override
    public String toString() {
        return "Credentials{email=" + email + ", passwordHash=[PROTECTED]}";
    }
}
