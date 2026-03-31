package com.huerto.api.application.impl.order;

import com.huerto.api.application.usecase.order.ListOrdersUseCase;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.Order;
import com.huerto.api.domain.ports.out.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListOrdersUseCaseImpl implements ListOrdersUseCase {

    private final OrderRepository orderRepository;

    public ListOrdersUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Page<Order> execute(OrderStatus status, Pageable pageable) {
        if (status != null)
            return orderRepository.findByStatus(status, pageable);
        return orderRepository.findAll(pageable);
    }
}