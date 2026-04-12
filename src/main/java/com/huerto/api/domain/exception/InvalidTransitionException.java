package com.huerto.api.domain.exception;

import com.huerto.api.domain.enums.OrderStatus;

public class InvalidTransitionException extends RuntimeException {

    public InvalidTransitionException(OrderStatus from, OrderStatus to) {
        super("Cannot transition order from %s to %s".formatted(from, to));
    }
}