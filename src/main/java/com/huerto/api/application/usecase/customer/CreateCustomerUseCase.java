package com.huerto.api.application.usecase.customer;

import com.huerto.api.application.commands.CreateCustomerCommand;
import com.huerto.api.domain.model.Customer;

public interface CreateCustomerUseCase {
    Customer execute(CreateCustomerCommand command);
}