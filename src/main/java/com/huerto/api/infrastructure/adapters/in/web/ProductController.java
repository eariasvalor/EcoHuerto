package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.commands.CreateProductCommand;
import com.huerto.api.application.commands.UpdateProductCommand;
import com.huerto.api.application.commands.UpdateStockCommand;
import com.huerto.api.application.usecase.product.*;
import com.huerto.api.infrastructure.adapters.in.web.dto.ProductRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.ProductResponse;
import com.huerto.api.infrastructure.adapters.in.web.dto.StockRequest;
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
    private final UpdateStockUseCase updateStockUseCase;

    public ProductController(CreateProductUseCase createProductUseCase,
                             ListProductsUseCase listProductsUseCase,
                             FindProductUseCase findProductUseCase,
                             UpdateProductUseCase updateProductUseCase,
                             UpdateStockUseCase updateStockUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.findProductUseCase = findProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.updateStockUseCase = updateStockUseCase;
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

    @PatchMapping("/{id}/stock")
    public ProductResponse updateStock(@PathVariable UUID id,
                                       @Valid @RequestBody StockRequest request) {
        UpdateStockCommand command = new UpdateStockCommand(id, request.quantity());
        return ProductResponse.from(updateStockUseCase.execute(command));
    }

}