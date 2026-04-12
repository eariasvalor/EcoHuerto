package com.huerto.api.application.impl.product;

import com.huerto.api.application.usecase.product.DeleteProductImageUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.ports.out.ImageStoragePort;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteProductImageUseCaseImpl implements DeleteProductImageUseCase {

    private final ProductRepository productRepository;
    private final ImageStoragePort imageStoragePort;

    public DeleteProductImageUseCaseImpl(ProductRepository productRepository,
                                         ImageStoragePort imageStoragePort) {
        this.productRepository = productRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    public void execute(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        if (product.imageUrl() != null) {
            imageStoragePort.delete(productId.toString());
        }

        productRepository.save(product.withImageUrl(null));
    }
}