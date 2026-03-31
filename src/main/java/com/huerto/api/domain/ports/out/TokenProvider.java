package com.huerto.api.domain.ports.out;

import java.util.UUID;

public interface TokenProvider {
    String generateToken(UUID userId, String email, String role);
}