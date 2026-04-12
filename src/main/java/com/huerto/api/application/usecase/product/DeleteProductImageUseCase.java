package com.huerto.api.application.usecase.product;

import java.util.UUID;

public interface DeleteProductImageUseCase {
    void execute(UUID productId);
}