package com.huerto.api.application.impl.product;

import com.huerto.api.application.usecase.product.ListAllProductsUseCase;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListAllProductsUseCaseImpl implements ListAllProductsUseCase {

    private final ProductRepository productRepository;

    public ListAllProductsUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> execute(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}