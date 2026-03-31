package com.huerto.api.application;

import com.huerto.api.application.impl.order.ListOrdersUseCaseImpl;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.OrderRepository;
import com.huerto.api.domain.valueobject.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListOrdersUseCaseTest {

    @Mock OrderRepository orderRepository;
    @InjectMocks ListOrdersUseCaseImpl listOrdersUseCase;

    private Order buildOrder() {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato");
        Product product = new Product(
                UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, 0
        );
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(
                UUID.randomUUID(), "HUE-0001", UUID.randomUUID(),
                List.of(line), OrderStatus.PENDING_CONFIRMATION,
                LocalDateTime.now(), 0
        );
    }

    @Test
    void should_return_all_orders_when_status_is_null() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Order order = buildOrder();
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAll(pageable)).thenReturn(page);

        Page<Order> result = listOrdersUseCase.execute(null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findAll(pageable);
        verify(orderRepository, never()).findByStatus(any(), any());
    }

    @Test
    void should_return_filtered_orders_when_status_is_provided() {
        Pageable pageable = PageRequest.of(0, 10);
        Order order = buildOrder();
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findByStatus(OrderStatus.PENDING_CONFIRMATION, pageable))
                .thenReturn(page);

        Page<Order> result = listOrdersUseCase.execute(
                OrderStatus.PENDING_CONFIRMATION, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findByStatus(OrderStatus.PENDING_CONFIRMATION, pageable);
        verify(orderRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void should_return_empty_page_when_no_orders() {
        Pageable pageable = PageRequest.of(0, 10);

        when(orderRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<Order> result = listOrdersUseCase.execute(null, pageable);

        assertThat(result.getContent()).isEmpty();
    }
}
