package com.huerto.api.application;

import com.huerto.api.application.impl.order.MarkReadyUseCaseImpl;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.InvalidStatusTransitionException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.OrderRepository;
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
class MarkReadyUseCaseTest {

    @Mock OrderRepository orderRepository;
    @InjectMocks MarkReadyUseCaseImpl markReadyUseCase;

    private Order buildOrder(UUID id, OrderStatus status) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato");
        Product product = new Product(
                UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, 0
        );
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(
                id, "HUE-0001", UUID.randomUUID(),
                List.of(line), status, LocalDateTime.now(), 0
        );
    }

    @Test
    void should_mark_ready_when_in_preparation() {
        UUID id = UUID.randomUUID();
        Order order = buildOrder(id, OrderStatus.IN_PREPARATION);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = markReadyUseCase.execute(id);

        assertThat(result.status()).isEqualTo(OrderStatus.READY_FOR_PICKUP);
        verify(orderRepository).save(any());
    }

    @Test
    void should_throw_when_order_not_found() {
        UUID id = UUID.randomUUID();

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        ThrowingCallable execute = () -> markReadyUseCase.execute(id);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void should_throw_when_order_is_not_in_preparation() {
        UUID id = UUID.randomUUID();
        Order order = buildOrder(id, OrderStatus.CONFIRMED);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        ThrowingCallable execute = () -> markReadyUseCase.execute(id);

        assertThatThrownBy(execute).isInstanceOf(InvalidStatusTransitionException.class);
        verify(orderRepository, never()).save(any());
    }
}