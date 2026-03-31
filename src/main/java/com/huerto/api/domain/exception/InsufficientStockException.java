package com.huerto.api.domain.exception;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(UUID productId, int available, int requested) {
        super("Insufficient stock for product " + productId
                + ". Available: " + available
                + ", requested: " + requested);
    }
}
