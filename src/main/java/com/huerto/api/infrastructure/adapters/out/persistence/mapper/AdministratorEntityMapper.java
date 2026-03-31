package com.huerto.api.infrastructure.adapters.out.persistence.mapper;

import com.huerto.api.domain.model.Administrator;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.AdministratorEntity;
import org.springframework.stereotype.Component;

@Component
public class AdministratorEntityMapper {

    public AdministratorEntity toEntity(Administrator admin) {
        AdministratorEntity entity = new AdministratorEntity();
        entity.setId(admin.id());
        entity.setName(admin.name());
        entity.setEmail(admin.credentials().email().value());
        entity.setPasswordHash(admin.credentials().passwordHash());
        entity.setPermission(admin.permission());
        entity.setActive(admin.active());
        entity.setCreatedAt(admin.createdAt());
        entity.setVersion(admin.version());
        return entity;
    }

    public Administrator toDomain(AdministratorEntity entity) {
        Credentials credentials = new Credentials(
                new Email(entity.getEmail()),
                entity.getPasswordHash()
        );
        return new Administrator(
                entity.getId(),
                entity.getName(),
                credentials,
                entity.getPermission(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getVersion()
        );
    }
}