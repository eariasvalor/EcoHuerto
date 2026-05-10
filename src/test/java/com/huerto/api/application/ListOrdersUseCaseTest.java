package com.huerto.api.application;

import com.huerto.api.application.impl.order.ListOrdersUseCaseImpl;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.OrderRepository;
import com.huerto.api.domain.valueobject.Description;
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
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product = new Product(
                UUID.randomUUID(), "Tomato", new Description("Fresh tomato"), variety,
                Price.of("2.50"), Unit.KG, 100, true, null,0
        );
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(
                UUID.randomUUID(), "HUE-0001", UUID.randomUUID(), "",
                List.of(line), OrderStatus.PENDING,
                LocalDateTime.now(), 0
        );
    }

    @Test
    void should_return_all_orders_when_no_filters() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Order order = buildOrder();
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAll(null, null, pageable)).thenReturn(page);

        Page<Order> result = listOrdersUseCase.execute(null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findAll(null, null, pageable);
    }

    @Test
    void should_filter_by_status() {
        Pageable pageable = PageRequest.of(0, 10);
        Order order = buildOrder();
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAll(OrderStatus.PENDING, null, pageable))
                .thenReturn(page);

        Page<Order> result = listOrdersUseCase.execute(
                OrderStatus.PENDING, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findAll(OrderStatus.PENDING, null, pageable);
    }

    @Test
    void should_filter_by_customer_id() {
        UUID customerId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Order order = buildOrder();
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAll(null, customerId, pageable)).thenReturn(page);

        Page<Order> result = listOrdersUseCase.execute(null, customerId, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findAll(null, customerId, pageable);
    }

    @Test
    void should_return_empty_page_when_no_orders() {
        Pageable pageable = PageRequest.of(0, 10);

        when(orderRepository.findAll(null, null, pageable)).thenReturn(Page.empty(pageable));

        Page<Order> result = listOrdersUseCase.execute(null, null, pageable);

        assertThat(result.getContent()).isEmpty();
    }
}