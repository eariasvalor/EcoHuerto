package com.huerto.api.application.impl.order;

import com.huerto.api.application.usecase.order.ConfirmOrderUseCase;
import com.huerto.api.domain.events.OrderStatusChangedEvent;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Order;
import com.huerto.api.domain.model.OrderLine;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.ports.out.EventPublisher;
import com.huerto.api.domain.ports.out.OrderRepository;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class ConfirmOrderUseCaseImpl implements ConfirmOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;


    public ConfirmOrderUseCaseImpl(OrderRepository orderRepository, ProductRepository productRepository, EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Order execute(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        Order confirmed = order.confirm();

        for (OrderLine line : order.lines()) {
            Product product = productRepository.findById(line.product().id())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", line.product().id()));
            productRepository.save(product.decreaseStock(line.quantity()));
        }

        Order saved = orderRepository.save(confirmed);

        eventPublisher.publish(new OrderStatusChangedEvent(
                saved, order.status(), saved.status(), LocalDateTime.now()));

        return saved;
    }
}