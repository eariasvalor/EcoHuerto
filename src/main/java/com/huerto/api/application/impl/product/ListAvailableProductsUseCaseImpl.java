package com.huerto.api.application.impl.product;

import com.huerto.api.application.usecase.product.ListAvailableProductsUseCase;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListAvailableProductsUseCaseImpl implements ListAvailableProductsUseCase {

    private final ProductRepository productRepository;

    public ListAvailableProductsUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> execute(Pageable pageable) {
        return productRepository.findAllAvailable(pageable);
    }
}