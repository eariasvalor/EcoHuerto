package com.huerto.api.application.usecase.customer;

import com.huerto.api.application.commands.RegisterCustomerCommand;
import com.huerto.api.domain.model.Customer;

public interface RegisterCustomerUseCase {
    Customer execute(RegisterCustomerCommand command);
}