package com.huerto.api.infrastructure.adapters.out.persistence.mapper;

import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import com.huerto.api.domain.valueobject.PhoneNumber;
import com.huerto.api.domain.valueobject.PostalAddress;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.CustomerEntity;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.PhoneNumberEmbeddable;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.PostalAddressEmbeddable;
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

        if (customer.phone() != null) {
            PhoneNumberEmbeddable phone = new PhoneNumberEmbeddable();
            phone.setCountryCode(customer.phone().countryCode());
            phone.setNumber(customer.phone().number());
            entity.setPhone(phone);
        }

        if (customer.address() != null) {
            PostalAddressEmbeddable address = new PostalAddressEmbeddable();
            address.setStreetType(customer.address().streetType());
            address.setStreet(customer.address().street());
            address.setNumber(customer.address().number());
            address.setFloor(customer.address().floor());
            address.setCity(customer.address().city());
            address.setPostalCode(customer.address().postalCode());
            address.setProvince(customer.address().province());
            entity.setAddress(address);
        }

        return entity;
    }

    public Customer toDomain(CustomerEntity entity) {
        Credentials credentials = new Credentials(
                new Email(entity.getEmail()),
                entity.getPasswordHash()
        );

        PhoneNumber phone = null;
        if (entity.getPhone() != null) {
            phone = new PhoneNumber(
                    entity.getPhone().getCountryCode(),
                    entity.getPhone().getNumber()
            );
        }

        PostalAddress address = null;
        if (entity.getAddress() != null && entity.getAddress().getCity() != null) {
            address = new PostalAddress(
                    entity.getAddress().getStreetType(),
                    entity.getAddress().getStreet(),
                    entity.getAddress().getNumber(),
                    entity.getAddress().getFloor(),
                    entity.getAddress().getCity(),
                    entity.getAddress().getPostalCode(),
                    entity.getAddress().getProvince()
            );
        }

        return new Customer(
                entity.getId(),
                entity.getName(),
                credentials,
                phone,
                address,
                entity.getCreatedAt(),
                entity.getVersion()
        );
    }
}