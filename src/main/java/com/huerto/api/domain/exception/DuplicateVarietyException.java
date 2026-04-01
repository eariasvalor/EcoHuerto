package com.huerto.api.domain.exception;

public class DuplicateVarietyException extends RuntimeException {
    public DuplicateVarietyException(String name, String productCategory) {
        super("Variety '" + name + "' already exists in category '" + productCategory + "'");
    }
}
