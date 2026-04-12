package com.huerto.api.application;

import com.huerto.api.application.impl.order.GetOrderStatsUseCaseImpl;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.OrderStats;
import com.huerto.api.domain.ports.out.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOrderStatsUseCaseTest {

    @Mock OrderRepository orderRepository;
    @InjectMocks GetOrderStatsUseCaseImpl getOrderStatsUseCase;

    @Test
    void should_return_order_stats_grouped_by_status() {
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(3L);
        when(orderRepository.countByStatus(OrderStatus.CONFIRMED)).thenReturn(5L);
        when(orderRepository.countByStatus(OrderStatus.READY_FOR_PICKUP)).thenReturn(1L);
        when(orderRepository.countByStatus(OrderStatus.DELIVERED)).thenReturn(2L);
        when(orderRepository.countByStatus(OrderStatus.CANCELLED)).thenReturn(8L);

        OrderStats stats = getOrderStatsUseCase.execute();

        assertThat(stats.pending()).isEqualTo(3L);
        assertThat(stats.confirmed()).isEqualTo(5L);
        assertThat(stats.ready()).isEqualTo(1L);
        assertThat(stats.delivered()).isEqualTo(2L);
        assertThat(stats.cancelled()).isEqualTo(8L);
        assertThat(stats.total()).isEqualTo(19L);
    }
}