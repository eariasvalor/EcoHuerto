package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<Customer> findAll(Pageable pageable);
}