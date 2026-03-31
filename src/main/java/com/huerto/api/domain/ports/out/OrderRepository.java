package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    Optional<Order> findByVisibleId(String visibleId);
    List<Order> findByCustomerId(UUID customerId);
    List<Order> findAll();
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findAll(Pageable pageable);
}