package com.huerto.api.application.impl.product;

import com.huerto.api.application.usecase.product.ToggleAvailabilityUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ToggleAvailabilityUseCaseImpl implements ToggleAvailabilityUseCase {

    private final ProductRepository productRepository;

    public ToggleAvailabilityUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        return productRepository.save(product.toggleAvailability());
    }
}