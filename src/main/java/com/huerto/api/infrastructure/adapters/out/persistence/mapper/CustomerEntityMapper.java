package com.huerto.api.infrastructure.adapters.out.persistence.mapper;

import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerEntityMapper {

    public CustomerEntity toEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(customer.id());
        entity.setName(customer.name());
        entity.setEmail(customer.credentials().email().value());
        entity.setPasswordHash(customer.credentials().passwordHash());
        entity.setCreatedAt(customer.createdAt());
        entity.setVersion(customer.version());
        return entity;
    }

    public Customer toDomain(CustomerEntity entity) {
        Credentials credentials = new Credentials(
                new Email(entity.getEmail()),
                entity.getPasswordHash()
        );
        return new Customer(
                entity.getId(),
                entity.getName(),
                credentials,
                entity.getCreatedAt(),
                entity.getVersion()
        );
    }
}