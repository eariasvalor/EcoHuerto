package com.huerto.api.application.usecase.order;

import com.huerto.api.domain.model.Order;

public record CreateOrderResult(Order order, boolean possibleDuplicate) {}