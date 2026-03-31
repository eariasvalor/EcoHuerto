package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.model.Variety;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VarietyRepository {
    Variety save(Variety variety);
    Optional<Variety> findById(UUID id);
    Page<Variety> findAll(Pageable pageable);
    List<Variety> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
}