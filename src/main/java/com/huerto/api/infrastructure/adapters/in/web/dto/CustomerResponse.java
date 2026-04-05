package com.huerto.api.infrastructure.adapters.in.web.dto;

import com.huerto.api.domain.model.Customer;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt,
        String phoneCountryCode,
        String phoneNumber,
        String addressStreetType,
        String addressStreet,
        String addressNumber,
        String addressFloor,
        String addressCity,
        String addressPostalCode,
        String addressProvince
) {
    public static CustomerResponse from(Customer customer) {
        String phoneCountryCode = customer.phone() != null ? customer.phone().countryCode() : null;
        String phoneNumber = customer.phone() != null ? customer.phone().number() : null;

        String addressStreetType = customer.address() != null ? customer.address().streetType() : null;
        String addressStreet = customer.address() != null ? customer.address().street() : null;
        String addressNumber = customer.address() != null ? customer.address().number() : null;
        String addressFloor = customer.address() != null ? customer.address().floor() : null;
        String addressCity = customer.address() != null ? customer.address().city() : null;
        String addressPostalCode = customer.address() != null ? customer.address().postalCode() : null;
        String addressProvince = customer.address() != null ? customer.address().province() : null;

        return new CustomerResponse(
                customer.id(),
                customer.name(),
                customer.credentials().email().value(),
                customer.createdAt(),
                phoneCountryCode,
                phoneNumber,
                addressStreetType,
                addressStreet,
                addressNumber,
                addressFloor,
                addressCity,
                addressPostalCode,
                addressProvince
        );
    }
}