package com.huerto.api.application;

import com.huerto.api.application.impl.order.ListMyOrdersUseCaseImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListMyOrdersUseCaseTest {

    @Mock OrderRepository orderRepository;
    @InjectMocks ListMyOrdersUseCaseImpl listMyOrdersUseCase;

    private Order buildOrder(UUID customerId) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato");
        Product product = new Product(UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, 0);
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(UUID.randomUUID(), "HUE-0001", customerId,
                List.of(line), OrderStatus.PENDING_CONFIRMATION, LocalDateTime.now(), 0);
    }

    @Test
    void should_return_paginated_orders_for_customer() {
        UUID customerId = UUID.randomUUID();
        Order order = buildOrder(customerId);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findByCustomerId(customerId, pageable)).thenReturn(page);

        Page<Order> result = listMyOrdersUseCase.execute(customerId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).customerId()).isEqualTo(customerId);
        verify(orderRepository).findByCustomerId(customerId, pageable);
    }

    @Test
    void should_return_empty_page_when_no_orders() {
        UUID customerId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        when(orderRepository.findByCustomerId(customerId, pageable)).thenReturn(Page.empty(pageable));

        Page<Order> result = listMyOrdersUseCase.execute(customerId, pageable);

        assertThat(result.getContent()).isEmpty();
    }
}