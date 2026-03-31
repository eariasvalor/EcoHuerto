package com.huerto.api.application.impl.product;

import com.huerto.api.application.usecase.product.FindProductUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FindProductUseCaseImpl implements FindProductUseCase {

    private final ProductRepository productRepository;

    public FindProductUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}