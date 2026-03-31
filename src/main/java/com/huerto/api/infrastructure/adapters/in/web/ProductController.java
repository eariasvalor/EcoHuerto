package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.commands.CreateProductCommand;
import com.huerto.api.application.commands.UpdateProductCommand;
import com.huerto.api.application.usecase.product.CreateProductUseCase;
import com.huerto.api.application.usecase.product.FindProductUseCase;
import com.huerto.api.application.usecase.product.ListProductsUseCase;
import com.huerto.api.application.usecase.product.UpdateProductUseCase;
import com.huerto.api.infrastructure.adapters.in.web.dto.ProductRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final FindProductUseCase findProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase,
                             ListProductsUseCase listProductsUseCase,
                             FindProductUseCase findProductUseCase,
                             UpdateProductUseCase updateProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.findProductUseCase = findProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        CreateProductCommand command = new CreateProductCommand(
                request.name(),
                request.varietyId(),
                request.price(),
                request.unit(),
                request.stock()
        );
        return ProductResponse.from(createProductUseCase.execute(command));
    }

    @GetMapping
    public Page<ProductResponse> list(Pageable pageable) {
        return listProductsUseCase.execute(pageable)
                .map(ProductResponse::from);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable UUID id) {
        return ProductResponse.from(findProductUseCase.execute(id));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody ProductRequest request) {
        UpdateProductCommand command = new UpdateProductCommand(
                id,
                request.name(),
                request.varietyId(),
                request.price(),
                request.unit()
        );
        return ProductResponse.from(updateProductUseCase.execute(command));
    }

}