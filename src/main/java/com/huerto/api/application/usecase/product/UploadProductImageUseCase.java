package com.huerto.api.application.usecase.product;

import com.huerto.api.domain.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UploadProductImageUseCase {
    Product execute(UUID productId, MultipartFile file);
}