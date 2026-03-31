package com.huerto.api.application.usecase.customer;

import com.huerto.api.application.commands.UpdateCustomerCommand;
import com.huerto.api.domain.model.Customer;

public interface UpdateCustomerUseCase {
    Customer execute(UpdateCustomerCommand command);
}