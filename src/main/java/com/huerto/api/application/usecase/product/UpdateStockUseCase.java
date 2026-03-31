package com.huerto.api.application.usecase.product;

import com.huerto.api.application.commands.UpdateStockCommand;
import com.huerto.api.domain.model.Product;

public interface UpdateStockUseCase {
    Product execute(UpdateStockCommand command);
}