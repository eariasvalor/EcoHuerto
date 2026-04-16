package com.huerto.api.domain.ports.out;

public interface EventPublisher {
    void publish(Object event);
}