package com.huerto.api.application.usecase.order;

import com.huerto.api.domain.model.Order;
import java.util.UUID;

public interface RevertOrderUseCase {
    Order execute(UUID orderId);
}