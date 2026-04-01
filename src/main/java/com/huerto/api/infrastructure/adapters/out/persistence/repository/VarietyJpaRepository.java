package com.huerto.api.infrastructure.adapters.out.persistence.repository;

import com.huerto.api.infrastructure.adapters.out.persistence.entity.VarietyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface VarietyJpaRepository extends JpaRepository<VarietyEntity, UUID> {
    boolean existsByNameAndProductCategory(String name, String productCategory);
}