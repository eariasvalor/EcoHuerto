package com.huerto.api.domain.model;

import com.huerto.api.domain.valueobject.Credentials;

import java.time.LocalDateTime;
import java.util.UUID;

public record Customer(
        UUID id,
        String name,
        Credentials credentials,
        LocalDateTime createdAt,
        int version
) {
    public Customer {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Customer name must not be blank");
        if (credentials == null)
            throw new IllegalArgumentException("Credentials must not be null");
        name = name.trim();
    }

    public Customer updateName(String newName) {
        if (newName == null || newName.isBlank())
            throw new IllegalArgumentException("Name must not be blank");
        return new Customer(id, newName.trim(), credentials, createdAt, version);
    }

    public Customer updateCredentials(Credentials newCredentials) {
        if (newCredentials == null)
            throw new IllegalArgumentException("Credentials must not be null");
        return new Customer(id, name, newCredentials, createdAt, version);
    }
}
