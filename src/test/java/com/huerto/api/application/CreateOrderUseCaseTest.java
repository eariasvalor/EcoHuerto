package com.huerto.api.application;

import com.huerto.api.application.commands.CreateOrderCommand;
import com.huerto.api.application.impl.order.CreateOrderUseCaseImpl;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.InsufficientStockException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.OrderRepository;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import com.huerto.api.domain.valueobject.Price;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock OrderRepository orderRepository;
    @Mock ProductRepository productRepository;
    @Mock CustomerRepository customerRepository;
    @InjectMocks CreateOrderUseCaseImpl createOrderUseCase;

    private Customer buildCustomer(UUID id) {
        return new Customer(id, "John", new Credentials(
                new Email("john@huerto.com"), "hash"), LocalDateTime.now(), 0);
    }

    private Product buildProduct(UUID id, int stock) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato");
        return new Product(id, "Tomato", variety, Price.of("2.50"), Unit.KG, stock, true, 0);
    }

    @Test
    void should_create_order_when_stock_is_sufficient() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Customer customer = buildCustomer(customerId);
        Product product = buildProduct(productId, 100);

        CreateOrderCommand command = new CreateOrderCommand(
                customerId, List.of(new CreateOrderCommand.OrderLineCommand(productId, 2))
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = createOrderUseCase.execute(command);

        assertThat(result.lines()).hasSize(1);
        assertThat(result.status().name()).isEqualTo("PENDING_CONFIRMATION");
        assertThat(result.total().amount()).isEqualByComparingTo("5.00");
        verify(orderRepository).save(any());
    }

    @Test
    void should_throw_when_customer_not_found() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                customerId, List.of(new CreateOrderCommand.OrderLineCommand(productId, 2))
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        ThrowingCallable execute = () -> createOrderUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void should_throw_when_product_not_found() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Customer customer = buildCustomer(customerId);

        CreateOrderCommand command = new CreateOrderCommand(
                customerId, List.of(new CreateOrderCommand.OrderLineCommand(productId, 2))
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ThrowingCallable execute = () -> createOrderUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void should_throw_when_stock_is_insufficient() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Customer customer = buildCustomer(customerId);
        Product product = buildProduct(productId, 1);

        CreateOrderCommand command = new CreateOrderCommand(
                customerId, List.of(new CreateOrderCommand.OrderLineCommand(productId, 10))
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ThrowingCallable execute = () -> createOrderUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(InsufficientStockException.class);
        verify(orderRepository, never()).save(any());
    }
}