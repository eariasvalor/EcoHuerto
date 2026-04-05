package com.huerto.api.domain.model;

import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.PhoneNumber;
import com.huerto.api.domain.valueobject.PostalAddress;

import java.time.LocalDateTime;
import java.util.UUID;

public record Customer(
        UUID id,
        String name,
        Credentials credentials,
        PhoneNumber phone,
        PostalAddress address,
        LocalDateTime createdAt,
        int version
) {
    public Customer {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Customer name must not be blank");
        if (credentials == null)
            throw new IllegalArgumentException("Credentials must not be null");
        if (phone == null)
            throw new IllegalArgumentException("Phone must not be null");
        name = name.trim();
    }

    public Customer updateName(String newName) {
        if (newName == null || newName.isBlank())
            throw new IllegalArgumentException("Name must not be blank");
        return new Customer(id, newName.trim(), credentials, phone, address, createdAt, version);
    }

    public Customer updateCredentials(Credentials newCredentials) {
        if (newCredentials == null)
            throw new IllegalArgumentException("Credentials must not be null");
        return new Customer(id, name, newCredentials, phone, address, createdAt, version);
    }

    public Customer updatePhone(PhoneNumber newPhone) {
        if (newPhone == null)
            throw new IllegalArgumentException("Phone must not be null");
        return new Customer(id, name, credentials, newPhone, address, createdAt, version);
    }

    public Customer updateAddress(PostalAddress newAddress) {
        return new Customer(id, name, credentials, phone, newAddress, createdAt, version);
    }
}
