package com.huerto.api.application.impl.order;

import com.fasterxml.uuid.Generators;
import com.huerto.api.application.commands.CreateOrderCommand;
import com.huerto.api.application.usecase.order.CreateOrderResult;
import com.huerto.api.application.usecase.order.CreateOrderUseCase;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.events.OrderCreatedEvent;
import com.huerto.api.domain.exception.InsufficientStockException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.EventPublisher;
import com.huerto.api.domain.ports.out.OrderRepository;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final EventPublisher eventPublisher;


    public CreateOrderUseCaseImpl(OrderRepository orderRepository,
                                  ProductRepository productRepository,
                                  CustomerRepository customerRepository,
                                  EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CreateOrderResult execute(CreateOrderCommand command) {
        customerRepository.findById(command.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", command.customerId()));

        List<OrderLine> lines = buildLines(command.lines());

        boolean possibleDuplicate = isDuplicate(command);

        Order order = new Order(
                Generators.timeBasedEpochGenerator().generate(),
                generateVisibleId(),
                command.customerId(),
                "",
                lines,
                OrderStatus.PENDING,
                LocalDateTime.now(),
                0
        );

        Order saved = orderRepository.save(order);
        eventPublisher.publish(new OrderCreatedEvent(saved, LocalDateTime.now()));

        return new CreateOrderResult(saved, possibleDuplicate);
    }

    private boolean isDuplicate(CreateOrderCommand command) {
        List<Order> pendingOrders = orderRepository.findByCustomerIdAndStatus(
                command.customerId(), OrderStatus.PENDING);

        return pendingOrders.stream().anyMatch(existing ->
                hasSameLines(existing.lines(), command.lines())
        );
    }

    private boolean hasSameLines(List<OrderLine> existingLines,
                                 List<CreateOrderCommand.OrderLineCommand> newLines) {
        if (existingLines.size() != newLines.size()) return false;

        return newLines.stream().allMatch(newLine ->
                existingLines.stream().anyMatch(existing ->
                        existing.product().id().equals(newLine.productId()) &&
                                existing.quantity() == newLine.quantity()
                )
        );
    }

    private List<OrderLine> buildLines(List<CreateOrderCommand.OrderLineCommand> lineCommands) {
        List<OrderLine> lines = new ArrayList<>();
        for (CreateOrderCommand.OrderLineCommand cmd : lineCommands) {
            Product product = productRepository.findById(cmd.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", cmd.productId()));

            if (!product.hasStock(cmd.quantity()))
                throw new InsufficientStockException(product.id(), product.stock(), cmd.quantity());

            lines.add(new OrderLine(
                    Generators.timeBasedEpochGenerator().generate(),
                    product,
                    cmd.quantity()
            ));
        }
        return lines;
    }

    private String generateVisibleId() {
        return "HUE-" + String.format("%04d", (int)(Math.random() * 9000) + 1000);
    }
}