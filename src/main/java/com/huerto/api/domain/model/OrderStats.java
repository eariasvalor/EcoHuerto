package com.huerto.api.domain.model;

public record OrderStats(
        long pending,
        long confirmed,
        long ready,
        long delivered,
        long cancelled,
        long total
) {}