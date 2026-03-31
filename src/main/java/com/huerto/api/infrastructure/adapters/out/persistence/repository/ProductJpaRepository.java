package com.huerto.api.infrastructure.adapters.out.persistence.repository;

import com.huerto.api.infrastructure.adapters.out.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    Page<ProductEntity> findByAvailableTrue(Pageable pageable);
    List<ProductEntity> findByVarietyId(UUID varietyId);
    boolean existsByVarietyId(UUID varietyId);
}