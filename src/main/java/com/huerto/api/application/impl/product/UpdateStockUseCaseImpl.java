package com.huerto.api.application.impl.product;

import com.huerto.api.application.commands.UpdateStockCommand;
import com.huerto.api.application.usecase.product.UpdateStockUseCase;
import com.huerto.api.domain.exception.InsufficientStockException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateStockUseCaseImpl implements UpdateStockUseCase {

    private final ProductRepository productRepository;

    public UpdateStockUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(UpdateStockCommand command) {
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", command.productId()));

        Product updated = command.quantity() > 0
                ? product.increaseStock(command.quantity())
                : decreaseStock(product, Math.abs(command.quantity()));

        return productRepository.save(updated);
    }

    private Product decreaseStock(Product product, int quantity) {
        if (!product.hasStock(quantity))
            throw new InsufficientStockException(product.id(), product.stock(), quantity);
        return product.decreaseStock(quantity);
    }
}