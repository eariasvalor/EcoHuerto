package com.huerto.api.application;

import com.huerto.api.application.impl.order.ConfirmOrderUseCaseImpl;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.InvalidStatusTransitionException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.OrderRepository;
import com.huerto.api.domain.ports.out.ProductRepository;
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
class ConfirmOrderUseCaseTest {

    @Mock OrderRepository orderRepository;
    @Mock
    ProductRepository productRepository;
    @InjectMocks ConfirmOrderUseCaseImpl confirmOrderUseCase;


    private Order buildOrder(UUID id, OrderStatus status) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product = new Product(
                UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, null, 0
        );
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(
                id, "HUE-0001", UUID.randomUUID(), "",
                List.of(line), status, LocalDateTime.now(), 0
        );
    }

    @Test
    void should_confirm_order_when_pending() {
        UUID id = UUID.randomUUID();
        Order order = buildOrder(id, OrderStatus.PENDING);
        Product product = order.lines().get(0).product();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(productRepository.findById(product.id())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = confirmOrderUseCase.execute(id);

        assertThat(result.status()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderRepository).save(any());
        verify(productRepository).save(any());
    }

    @Test
    void should_throw_when_order_not_found() {
        UUID id = UUID.randomUUID();

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        ThrowingCallable execute = () -> confirmOrderUseCase.execute(id);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void should_throw_when_order_already_confirmed() {
        UUID id = UUID.randomUUID();
        Order order = buildOrder(id, OrderStatus.CONFIRMED);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        ThrowingCallable execute = () -> confirmOrderUseCase.execute(id);

        assertThatThrownBy(execute)
                .isInstanceOf(InvalidStatusTransitionException.class);
        verify(orderRepository, never()).save(any());
    }
}