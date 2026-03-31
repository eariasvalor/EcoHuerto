package com.huerto.api.infrastructure.adapters.out.persistence.repository;

import com.huerto.api.infrastructure.adapters.out.persistence.entity.AdministratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AdministratorJpaRepository extends JpaRepository<AdministratorEntity, UUID> {
    Optional<AdministratorEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
