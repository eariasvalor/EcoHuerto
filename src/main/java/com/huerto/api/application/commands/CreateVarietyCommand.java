package com.huerto.api.application.commands;

public record CreateVarietyCommand(
        String name,
        String productCategory
) {
    public CreateVarietyCommand {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name must not be blank");
        if (productCategory == null || productCategory.isBlank())
            throw new IllegalArgumentException("Product category must not be blank");
    }
}