package com.huerto.api.infrastructure.adapters.out.persistence.adapter;

import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.infrastructure.adapters.out.persistence.mapper.CustomerEntityMapper;
import com.huerto.api.infrastructure.adapters.out.persistence.repository.CustomerJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerJpaAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerEntityMapper mapper;

    public CustomerJpaAdapter(CustomerJpaRepository jpaRepository,
                              CustomerEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Customer save(Customer customer) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(customer)));
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID customerId) {
        jpaRepository.deleteById(customerId);
    }
}
