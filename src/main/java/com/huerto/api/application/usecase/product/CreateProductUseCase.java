package com.huerto.api.application.usecase.product;

import com.huerto.api.application.commands.CreateProductCommand;
import com.huerto.api.domain.model.Product;

public interface CreateProductUseCase {
    Product execute(CreateProductCommand command);
}