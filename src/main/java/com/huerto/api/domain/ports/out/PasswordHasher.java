package com.huerto.api.domain.ports.out;

public interface PasswordHasher {
    String hash(String rawPassword);
    boolean verify(String rawPassword, String hash);
}
