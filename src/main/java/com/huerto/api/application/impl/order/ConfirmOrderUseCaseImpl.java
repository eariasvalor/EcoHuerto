package com.huerto.api.application.impl.order;

import com.huerto.api.application.usecase.order.ConfirmOrderUseCase;
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
public class ConfirmOrderUseCaseImpl implements ConfirmOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;


    public ConfirmOrderUseCaseImpl(OrderRepository orderRepository, EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Order execute(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        Order confirmed = order.confirm();

        // Stock was already decreased when the order was created (PENDING status)
        // No need to decrease it again here

        Order saved = orderRepository.save(confirmed);

        eventPublisher.publish(new OrderStatusChangedEvent(
                saved, order.status(), saved.status(), LocalDateTime.now()));

        return saved;
    }
}