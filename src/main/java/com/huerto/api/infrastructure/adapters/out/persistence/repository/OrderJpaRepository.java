package com.huerto.api.infrastructure.adapters.out.persistence.repository;

import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByVisibleId(String visibleId);
    List<OrderEntity> findByCustomerId(UUID customerId);
    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);
    List<OrderEntity> findByCustomerIdAndStatus(UUID customerId, OrderStatus status);
    Page<OrderEntity> findByCustomerId(UUID customerId, Pageable pageable);
    long countByStatus(OrderStatus status);
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.customer WHERE " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:customerId IS NULL OR o.customerId = :customerId)")
    Page<OrderEntity> findAllFiltered(
            @Param("status") OrderStatus status,
            @Param("customerId") UUID customerId,
            Pageable pageable
    );
}
