package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    List<Product> findAllAvailable();
    List<Product> findByVarietyId(UUID varietyId);
    boolean existsByVarietyId(UUID varietyId);
    void deleteById(UUID id);
}
