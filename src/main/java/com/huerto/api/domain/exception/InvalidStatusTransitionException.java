package com.huerto.api.domain.exception;

import com.huerto.api.domain.enums.OrderStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(OrderStatus current, OrderStatus next) {
        super("Invalid status transition from " + current + " to " + next);
    }
}
