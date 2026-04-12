package com.huerto.api.application.impl.order;

import com.huerto.api.application.usecase.order.DeliverOrderUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Order;
import com.huerto.api.domain.ports.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeliverOrderUseCaseImpl implements DeliverOrderUseCase {

    private final OrderRepository orderRepository;

    public DeliverOrderUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order execute(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        return orderRepository.save(order.deliver());
    }
}