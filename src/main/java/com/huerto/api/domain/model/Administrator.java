package com.huerto.api.domain.model;

import com.huerto.api.domain.enums.AdminPermission;
import com.huerto.api.domain.valueobject.Credentials;

import java.time.LocalDateTime;
import java.util.UUID;

public record Administrator(
        UUID id,
        String name,
        Credentials credentials,
        AdminPermission permission,
        boolean active,
        LocalDateTime createdAt,
        int version
) {
    public Administrator {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Administrator name must not be blank");
        if (credentials == null)
            throw new IllegalArgumentException("Credentials must not be null");
        if (permission == null)
            throw new IllegalArgumentException("Permission must not be null");
        name = name.trim();
    }

    public boolean canManageAdmins() {
        return this.permission == AdminPermission.OWNER;
    }

    public Administrator deactivate() {
        return new Administrator(id, name, credentials, permission,
                false, createdAt, version);
    }

    public Administrator activate() {
        return new Administrator(id, name, credentials, permission,
                true, createdAt, version);
    }
}
