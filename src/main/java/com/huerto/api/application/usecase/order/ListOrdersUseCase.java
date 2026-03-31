package com.huerto.api.application.usecase.order;

import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListOrdersUseCase {
    Page<Order> execute(OrderStatus status, Pageable pageable);
}