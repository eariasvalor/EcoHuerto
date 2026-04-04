package com.huerto.api.application.usecase.customer;

import java.util.UUID;

public interface DeleteCustomerUseCase {
    void execute(UUID customerId);
}