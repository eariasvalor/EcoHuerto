package com.huerto.api.application.impl.order;

import com.huerto.api.application.usecase.order.ConfirmOrderUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Order;
import com.huerto.api.domain.ports.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ConfirmOrderUseCaseImpl implements ConfirmOrderUseCase {

    private final OrderRepository orderRepository;

    public ConfirmOrderUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order execute(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        return orderRepository.save(order.confirm());
    }
}