package com.huerto.api.application.impl.order;

import com.huerto.api.application.usecase.order.FindOrderUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Order;
import com.huerto.api.domain.ports.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FindOrderUseCaseImpl implements FindOrderUseCase {

    private final OrderRepository orderRepository;

    public FindOrderUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order execute(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }
}