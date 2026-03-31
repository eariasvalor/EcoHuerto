package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.model.Administrator;
import java.util.Optional;
import java.util.UUID;

public interface AdministratorRepository {
    Administrator save(Administrator administrator);
    Optional<Administrator> findById(UUID id);
    Optional<Administrator> findByEmail(String email);
    boolean existsByEmail(String email);
}