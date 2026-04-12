package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.usecase.product.ListAllProductsUseCase;
import com.huerto.api.application.usecase.product.UploadProductImageUseCase;
import com.huerto.api.infrastructure.adapters.in.web.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/products")
@Tag(name = "Admin — Products", description = "Product management for administrators")
public class AdminProductController {

    private final ListAllProductsUseCase listAllProductsUseCase;
    private final UploadProductImageUseCase uploadProductImageUseCase;

    public AdminProductController(ListAllProductsUseCase listAllProductsUseCase, UploadProductImageUseCase uploadProductImageUseCase
                                  ) {
        this.listAllProductsUseCase = listAllProductsUseCase;
        this.uploadProductImageUseCase = uploadProductImageUseCase;
    }

    @GetMapping
    @Operation(summary = "List all products including unavailable")
    @ApiResponse(responseCode = "200", description = "Paginated full product list")
    public Page<ProductResponse> listAll(Pageable pageable) {
        return listAllProductsUseCase.execute(pageable).map(ProductResponse::from);
    }

    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload or replace the image for a product category")
    @ApiResponse(responseCode = "200", description = "Image uploaded")
    @ApiResponse(responseCode = "400", description = "File is empty")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ProductResponse uploadImage(@PathVariable UUID id,
                                       @RequestParam("file") MultipartFile file) {
        if (file.isEmpty())
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "File must not be empty");

        return ProductResponse.from(uploadProductImageUseCase.execute(id, file));
    }
}