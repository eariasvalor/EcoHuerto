package com.huerto.api.application.impl.order;

import com.huerto.api.application.usecase.order.RevertOrderUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Order;
import com.huerto.api.domain.ports.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RevertOrderUseCaseImpl implements RevertOrderUseCase {

    private final OrderRepository orderRepository;

    public RevertOrderUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order execute(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        return orderRepository.save(order.revert());
    }
}