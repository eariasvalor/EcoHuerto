package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.usecase.product.ListAllProductsUseCase;
import com.huerto.api.infrastructure.adapters.in.web.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
@Tag(name = "Admin — Products", description = "Product management for administrators")
public class AdminProductController {

    private final ListAllProductsUseCase listAllProductsUseCase;

    public AdminProductController(ListAllProductsUseCase listAllProductsUseCase) {
        this.listAllProductsUseCase = listAllProductsUseCase;
    }

    @GetMapping
    @Operation(summary = "List all products including unavailable")
    @ApiResponse(responseCode = "200", description = "Paginated full product list")
    public Page<ProductResponse> listAll(Pageable pageable) {
        return listAllProductsUseCase.execute(pageable).map(ProductResponse::from);
    }
}