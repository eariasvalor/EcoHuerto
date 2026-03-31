package com.huerto.api.application.usecase.product;

import java.util.UUID;

public interface DeleteProductUseCase {
    void execute(UUID id);
}
