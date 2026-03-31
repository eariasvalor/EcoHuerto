package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    Optional<Order> findByVisibleId(String visibleId);
    List<Order> findByCustomerId(UUID customerId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findAll();
}