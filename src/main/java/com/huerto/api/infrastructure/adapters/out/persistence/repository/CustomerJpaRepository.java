package com.huerto.api.infrastructure.adapters.out.persistence.repository;

import com.huerto.api.infrastructure.adapters.out.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}