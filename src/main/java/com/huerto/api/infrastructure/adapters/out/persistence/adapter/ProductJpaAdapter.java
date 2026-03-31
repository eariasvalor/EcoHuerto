package com.huerto.api.infrastructure.adapters.out.persistence.adapter;

import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.ProductEntity;
import com.huerto.api.infrastructure.adapters.out.persistence.mapper.ProductEntityMapper;
import com.huerto.api.infrastructure.adapters.out.persistence.repository.ProductJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductJpaAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductEntityMapper mapper;

    public ProductJpaAdapter(ProductJpaRepository jpaRepository,
                             ProductEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Page<Product> findAllAvailable(Pageable pageable) {
        return jpaRepository.findByAvailableTrue(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public List<Product> findByVarietyId(UUID varietyId) {
        return jpaRepository.findByVarietyId(varietyId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByVarietyId(UUID varietyId) {
        return jpaRepository.existsByVarietyId(varietyId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}