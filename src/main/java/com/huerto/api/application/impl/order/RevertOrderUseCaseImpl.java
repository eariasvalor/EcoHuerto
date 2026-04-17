package com.huerto.api.application.impl.order;

import com.huerto.api.application.usecase.order.RevertOrderUseCase;
import com.huerto.api.domain.events.OrderStatusChangedEvent;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Order;
import com.huerto.api.domain.ports.out.EventPublisher;
import com.huerto.api.domain.ports.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class RevertOrderUseCaseImpl implements RevertOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    public RevertOrderUseCaseImpl(OrderRepository orderRepository, EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Order execute(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        Order saved = orderRepository.save(order.revert());

        eventPublisher.publish(new OrderStatusChangedEvent(
                saved, order.status(), saved.status(), LocalDateTime.now()));

        return saved;
    }
}