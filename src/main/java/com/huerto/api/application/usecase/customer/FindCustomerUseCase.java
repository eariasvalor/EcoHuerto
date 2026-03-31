package com.huerto.api.application.usecase.customer;

import com.huerto.api.domain.model.Customer;
import java.util.UUID;

public interface FindCustomerUseCase {
    Customer execute(UUID id);
}