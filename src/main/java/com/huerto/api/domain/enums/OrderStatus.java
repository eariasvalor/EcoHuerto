package com.huerto.api.domain.enums;

import java.util.Set;

public enum OrderStatus {

    PENDING {
        @Override
        public Set<OrderStatus> validTransitions() {
            return Set.of(CONFIRMED, CANCELLED);
        }

        @Override
        public boolean notifiesCustomer() { return true; }
    },

    CONFIRMED {
        @Override
        public Set<OrderStatus> validTransitions() {
            return Set.of(READY_FOR_PICKUP, CANCELLED);
        }

        @Override
        public boolean notifiesCustomer() { return true; }
    },

    READY_FOR_PICKUP {
        @Override
        public Set<OrderStatus> validTransitions() {
            return Set.of(DELIVERED);
        }

        @Override
        public boolean notifiesCustomer() { return true; }
    },

    DELIVERED {
        @Override
        public Set<OrderStatus> validTransitions() {
            return Set.of();
        }

        @Override
        public boolean notifiesCustomer() { return false; }
    },

    CANCELLED {
        @Override
        public Set<OrderStatus> validTransitions() {
            return Set.of();
        }

        @Override
        public boolean notifiesCustomer() { return true; }
    };

    public abstract Set<OrderStatus> validTransitions();
    public abstract boolean notifiesCustomer();

    public boolean canTransitionTo(OrderStatus target) {
        return validTransitions().contains(target);
    }
}