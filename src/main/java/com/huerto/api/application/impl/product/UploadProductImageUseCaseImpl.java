package com.huerto.api.application.impl.product;

import com.huerto.api.application.usecase.product.UploadProductImageUseCase;
import com.huerto.api.domain.exception.ImageUploadException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.ports.out.ImageStoragePort;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Transactional
public class UploadProductImageUseCaseImpl implements UploadProductImageUseCase {

    private static final String FOLDER = "huerto/categories";

    private final ProductRepository productRepository;
    private final ImageStoragePort imageStoragePort;

    public UploadProductImageUseCaseImpl(ProductRepository productRepository,
                                         ImageStoragePort imageStoragePort) {
        this.productRepository = productRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    public Product execute(UUID productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        if (product.imageUrl() != null) {
            imageStoragePort.delete(productId.toString());
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ImageUploadException("Failed to read image file", e);
        }

        ImageStoragePort.ImageUploadResult result =
                imageStoragePort.upload(bytes, file.getOriginalFilename(), FOLDER);

        return productRepository.save(product.withImageUrl(result.secureUrl()));
    }
}