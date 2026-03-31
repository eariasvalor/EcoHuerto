package com.huerto.api.application.impl.product;

import com.huerto.api.application.usecase.product.DeleteProductUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteProductUseCaseImpl implements DeleteProductUseCase {

    private final ProductRepository productRepository;

    public DeleteProductUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void execute(UUID id) {
        if (!productRepository.existsById(id))
            throw new ResourceNotFoundException("Product", id);

        productRepository.deleteById(id);
    }
}