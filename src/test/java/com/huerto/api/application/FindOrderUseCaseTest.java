package com.huerto.api.application;

import com.huerto.api.application.impl.order.FindOrderUseCaseImpl;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindOrderUseCaseTest {

    @Mock OrderRepository orderRepository;
    @InjectMocks FindOrderUseCaseImpl findOrderUseCase;

    private Order buildOrder(UUID id) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato");
        Product product = new Product(
                UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, 0
        );
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(
                id, "HUE-0001", UUID.randomUUID(), "",
                List.of(line), OrderStatus.PENDING,
                LocalDateTime.now(), 0
        );
    }

    @Test
    void should_return_order_when_found() {
        UUID id = UUID.randomUUID();
        Order order = buildOrder(id);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        Order result = findOrderUseCase.execute(id);

        assertThat(result).isEqualTo(order);
        verify(orderRepository).findById(id);
    }

    @Test
    void should_throw_when_order_not_found() {
        UUID id = UUID.randomUUID();

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        ThrowingCallable execute = () -> findOrderUseCase.execute(id);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
