package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    Page<Product> findAllAvailable(Pageable pageable);
    List<Product> findByVarietyId(UUID varietyId);
    boolean existsByVarietyId(UUID varietyId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    Page<Product> findAll(Pageable pageable);
}
