package com.huerto.api.infrastructure.adapters.out.persistence.adapter;

import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.VarietyRepository;
import com.huerto.api.infrastructure.adapters.out.persistence.mapper.VarietyEntityMapper;
import com.huerto.api.infrastructure.adapters.out.persistence.repository.VarietyJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VarietyJpaAdapter implements VarietyRepository {

    private final VarietyJpaRepository jpaRepository;
    private final VarietyEntityMapper mapper;

    public VarietyJpaAdapter(VarietyJpaRepository jpaRepository,
                             VarietyEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Variety save(Variety variety) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(variety)));
    }

    @Override
    public Optional<Variety> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Variety> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Page<Variety> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}