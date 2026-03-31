package com.huerto.api.infrastructure.adapters.in.web.dto;

import com.huerto.api.domain.model.Customer;
import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt
) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.id(),
                customer.name(),
                customer.credentials().email().value(),
                customer.createdAt()
        );
    }
}