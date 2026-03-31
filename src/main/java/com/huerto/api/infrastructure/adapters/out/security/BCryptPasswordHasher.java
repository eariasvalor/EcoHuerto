package com.huerto.api.infrastructure.adapters.out.security;

import com.huerto.api.domain.ports.out.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {
    private static final BCryptPasswordEncoder ENCODER =
            new BCryptPasswordEncoder();

    @Override
    public String hash(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    @Override
    public boolean verify(String rawPassword, String hash) {
        return ENCODER.matches(rawPassword, hash);
    }
}
