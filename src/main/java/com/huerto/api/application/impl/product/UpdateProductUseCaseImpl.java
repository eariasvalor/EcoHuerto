package com.huerto.api.application.impl.product;

import com.huerto.api.application.commands.UpdateProductCommand;
import com.huerto.api.application.usecase.product.UpdateProductUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.ports.out.VarietyRepository;
import com.huerto.api.domain.valueobject.Price;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductUseCaseImpl implements UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final VarietyRepository varietyRepository;

    public UpdateProductUseCaseImpl(ProductRepository productRepository,
                                    VarietyRepository varietyRepository) {
        this.productRepository = productRepository;
        this.varietyRepository = varietyRepository;
    }

    @Override
    public Product execute(UpdateProductCommand command) {
        Product existing = productRepository.findById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("Product", command.id()));

        Variety variety = varietyRepository.findById(command.varietyId())
                .orElseThrow(() -> new ResourceNotFoundException("Variety", command.varietyId()));

        Product updated = new Product(
                existing.id(),
                command.name(),
                variety,
                Price.of(command.price()),
                command.unit(),
                existing.stock(),
                existing.available(),
                existing.version()
        );

        return productRepository.save(updated);
    }
}