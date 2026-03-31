package com.huerto.api.application.usecase.product;

import com.huerto.api.domain.model.Product;
import java.util.List;

public interface ListProductsUseCase {
    List<Product> execute();
}