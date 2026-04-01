package com.huerto.api.application.usecase.product;

import com.huerto.api.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListAllProductsUseCase {
    Page<Product> execute(Pageable pageable);
}