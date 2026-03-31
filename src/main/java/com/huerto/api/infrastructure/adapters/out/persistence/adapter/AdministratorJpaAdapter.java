package com.huerto.api.infrastructure.adapters.out.persistence.adapter;

import com.huerto.api.domain.model.Administrator;
import com.huerto.api.domain.ports.out.AdministratorRepository;
import com.huerto.api.infrastructure.adapters.out.persistence.mapper.AdministratorEntityMapper;
import com.huerto.api.infrastructure.adapters.out.persistence.repository.AdministratorJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AdministratorJpaAdapter implements AdministratorRepository {

    private final AdministratorJpaRepository jpaRepository;
    private final AdministratorEntityMapper mapper;

    public AdministratorJpaAdapter(AdministratorJpaRepository jpaRepository,
                                   AdministratorEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Administrator save(Administrator administrator) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(administrator)));
    }

    @Override
    public Optional<Administrator> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Administrator> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
