package com.huerto.api.application.impl.product;

import com.huerto.api.application.usecase.product.ListProductsUseCase;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListProductsUseCaseImpl implements ListProductsUseCase {

    private final ProductRepository productRepository;

    public ListProductsUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> execute() {
        return productRepository.findAllAvailable();
    }
}