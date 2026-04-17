package com.huerto.api.application.impl.order;

import com.huerto.api.application.usecase.order.DeliverOrderUseCase;
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
public class DeliverOrderUseCaseImpl implements DeliverOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    public DeliverOrderUseCaseImpl(OrderRepository orderRepository, EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Order execute(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        Order saved = orderRepository.save(order.deliver());

        eventPublisher.publish(new OrderStatusChangedEvent(
                saved, order.status(), saved.status(), LocalDateTime.now()));

        return saved;
    }
}