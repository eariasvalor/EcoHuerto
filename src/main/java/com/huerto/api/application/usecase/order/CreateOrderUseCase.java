package com.huerto.api.application.usecase.order;

import com.huerto.api.application.commands.CreateOrderCommand;
import com.huerto.api.domain.model.Order;

public interface CreateOrderUseCase {
    Order execute(CreateOrderCommand command);
}