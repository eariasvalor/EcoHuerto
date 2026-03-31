package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.commands.CreateProductCommand;
import com.huerto.api.application.commands.UpdateProductCommand;
import com.huerto.api.application.commands.UpdateStockCommand;
import com.huerto.api.application.usecase.product.*;
import com.huerto.api.infrastructure.adapters.in.web.dto.ProductRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.ProductResponse;
import com.huerto.api.infrastructure.adapters.in.web.dto.StockRequest;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product catalogue management")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final FindProductUseCase findProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;
    private final ToggleAvailabilityUseCase toggleAvailabilityUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase,
                             ListProductsUseCase listProductsUseCase,
                             FindProductUseCase findProductUseCase,
                             UpdateProductUseCase updateProductUseCase,
                             UpdateStockUseCase updateStockUseCase,
                             ToggleAvailabilityUseCase toggleAvailabilityUseCase,
                             DeleteProductUseCase deleteProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.findProductUseCase = findProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.updateStockUseCase = updateStockUseCase;
        this.toggleAvailabilityUseCase = toggleAvailabilityUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    @Operation(summary = "Create a new product")
            @ApiResponse(responseCode = "201", description = "Product created")
            @ApiResponse(responseCode = "400", description = "Invalid request body")
            @ApiResponse(responseCode = "404", description = "Variety not found")
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

    @Operation(summary = "List all available products",
            description = "Returns a paginated list of products with available=true")
    @ApiResponse(responseCode = "200", description = "Paginated product list")
    @GetMapping
    public Page<ProductResponse> list(Pageable pageable) {
        return listProductsUseCase.execute(pageable)
                .map(ProductResponse::from);
    }

    @Operation(summary = "Find a product by its ID")
            @ApiResponse(responseCode = "200", description = "Product found")
            @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable UUID id) {
        return ProductResponse.from(findProductUseCase.execute(id));
    }

    @Operation(summary = "Update a product")
            @ApiResponse(responseCode = "200", description = "Product updated")
            @ApiResponse(responseCode = "400", description = "Invalid request body")
            @ApiResponse(responseCode = "404", description = "Product or variety not found")
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

    @Operation(summary = "Update product stock",
            description = "Positive quantity increases stock, negative decreases it")
            @ApiResponse(responseCode = "200", description = "Stock updated")
            @ApiResponse(responseCode = "404", description = "Product not found")
            @ApiResponse(responseCode = "409", description = "Insufficient stock")
    @PatchMapping("/{id}/stock")
    public ProductResponse updateStock(@PathVariable UUID id,
                                       @Valid @RequestBody StockRequest request) {
        UpdateStockCommand command = new UpdateStockCommand(id, request.quantity());
        return ProductResponse.from(updateStockUseCase.execute(command));
    }

    @Operation(summary = "Toggle product availability",
            description = "Switches available between true and false")
            @ApiResponse(responseCode = "200", description = "Availability toggled")
            @ApiResponse(responseCode = "404", description = "Product not found")

    @PatchMapping("/{id}/availability")
    public ProductResponse toggleAvailability(@PathVariable UUID id) {
        return ProductResponse.from(toggleAvailabilityUseCase.execute(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a product")
            @ApiResponse(responseCode = "204", description = "Product deleted")
            @ApiResponse(responseCode = "404", description = "Product not found")
    public void delete(
            @Parameter(description = "Product UUID") @PathVariable UUID id) {
        deleteProductUseCase.execute(id);
    }

}