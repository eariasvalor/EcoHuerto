package com.huerto.api.application.usecase.product;

import com.huerto.api.application.commands.UpdateProductCommand;
import com.huerto.api.domain.model.Product;

public interface UpdateProductUseCase {
    Product execute(UpdateProductCommand command);
}