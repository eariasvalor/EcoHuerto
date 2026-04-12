package com.huerto.api.application.impl.product;

import com.fasterxml.uuid.Generators;
import com.huerto.api.application.commands.CreateProductCommand;
import com.huerto.api.application.usecase.product.CreateProductUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.ports.out.VarietyRepository;
import com.huerto.api.domain.valueobject.Price;
import org.springframework.stereotype.Service;

@Service
public class CreateProductUseCaseImpl implements CreateProductUseCase {

    private final ProductRepository productRepository;
    private final VarietyRepository varietyRepository;

    public CreateProductUseCaseImpl(ProductRepository productRepository,
                                    VarietyRepository varietyRepository) {
        this.productRepository = productRepository;
        this.varietyRepository = varietyRepository;
    }

    @Override
    public Product execute(CreateProductCommand command) {
        Variety variety = varietyRepository.findById(command.varietyId())
                .orElseThrow(() -> new ResourceNotFoundException("Variety", command.varietyId()));

        Product product = new Product(
                Generators.timeBasedEpochGenerator().generate(),
                command.name(),
                variety,
                Price.of(command.price()),
                command.unit(),
                command.stock(),
                true,
                null,
                0
        );

        return productRepository.save(product);
    }
}
