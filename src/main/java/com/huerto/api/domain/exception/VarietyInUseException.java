package com.huerto.api.domain.exception;

import java.util.UUID;

public class VarietyInUseException extends RuntimeException {
    public VarietyInUseException(UUID varietyId) {
        super("Variety " + varietyId + " cannot be deleted — it is assigned to one or more products");
    }
}
