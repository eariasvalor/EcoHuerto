package com.huerto.api.application.impl.order;

import com.huerto.api.application.usecase.order.GetOrderStatsUseCase;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.OrderStats;
import com.huerto.api.domain.ports.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetOrderStatsUseCaseImpl implements GetOrderStatsUseCase {

    private final OrderRepository orderRepository;

    public GetOrderStatsUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderStats execute() {
        long pending = orderRepository.countByStatus(OrderStatus.PENDING_CONFIRMATION);
        long confirmed = orderRepository.countByStatus(OrderStatus.CONFIRMED);
        long inPreparation = orderRepository.countByStatus(OrderStatus.IN_PREPARATION);
        long ready = orderRepository.countByStatus(OrderStatus.READY_FOR_PICKUP);
        long cancelled = orderRepository.countByStatus(OrderStatus.CANCELLED);
        long total = pending + confirmed + inPreparation + ready + cancelled;

        return new OrderStats(pending, confirmed, inPreparation, ready, cancelled, total);
    }
}