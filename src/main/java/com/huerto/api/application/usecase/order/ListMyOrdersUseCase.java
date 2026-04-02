package com.huerto.api.application.usecase.order;

import com.huerto.api.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListMyOrdersUseCase {
    Page<Order> execute(UUID customerId, Pageable pageable);
}