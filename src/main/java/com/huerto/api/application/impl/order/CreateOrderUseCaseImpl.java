package com.huerto.api.application.impl.order;

import com.fasterxml.uuid.Generators;
import com.huerto.api.application.commands.CreateOrderCommand;
import com.huerto.api.application.usecase.order.CreateOrderUseCase;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.exception.InsufficientStockException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.OrderRepository;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public CreateOrderUseCaseImpl(OrderRepository orderRepository,
                                  ProductRepository productRepository,
                                  CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Order execute(CreateOrderCommand command) {
        customerRepository.findById(command.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", command.customerId()));

        List<OrderLine> lines = buildLines(command.lines());

        Order order = new Order(
                Generators.timeBasedEpochGenerator().generate(),
                generateVisibleId(),
                command.customerId(),
                lines,
                OrderStatus.PENDING_CONFIRMATION,
                LocalDateTime.now(),
                0
        );

        return orderRepository.save(order);
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
        return "HUE-" + String.format("%04d",
                (int)(Math.random() * 9000) + 1000);
    }
}
