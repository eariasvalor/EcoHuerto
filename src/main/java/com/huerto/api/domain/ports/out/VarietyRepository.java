package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.model.Variety;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VarietyRepository {
    Variety save(Variety variety);
    Optional<Variety> findById(UUID id);
    List<Variety> findAll();
    boolean existsById(UUID id);
}