package com.huerto.api.domain.valueobject;

import java.util.Objects;

public record Description(String value) {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 1000;

    public Description {
        Objects.requireNonNull(value, "Description must not be null");
        value = value.trim();
        if (value.length() < MIN_LENGTH)
            throw new IllegalArgumentException("Description must have at least " + MIN_LENGTH + " characters");
        if (value.length() > MAX_LENGTH)
            throw new IllegalArgumentException("Description must not exceed " + MAX_LENGTH + " characters");
    }

    @Override
    public String toString() {
        return value;
    }
}