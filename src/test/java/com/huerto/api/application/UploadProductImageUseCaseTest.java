package com.huerto.api.application;

import com.huerto.api.application.impl.product.UploadProductImageUseCaseImpl;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ImageStoragePort;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.valueobject.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadProductImageUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock ImageStoragePort imageStoragePort;
    @InjectMocks UploadProductImageUseCaseImpl uploadProductImageUseCase;

    private Product buildProduct(UUID id) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        return new Product(id, "Tomato", variety, Price.of("2.50"),
                Unit.KG, 100, true, null, 0);
    }

    @Test
    void should_upload_image_and_persist_url() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(id);
        String imageUrl = "https://res.cloudinary.com/huerto/image/upload/huerto/categories/abc.jpg";

        MockMultipartFile file = new MockMultipartFile(
                "file", "tomato.jpg", MediaType.IMAGE_JPEG_VALUE, "image-bytes".getBytes()
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(imageStoragePort.upload(any(), eq("tomato.jpg"), eq("huerto/categories")))
                .thenReturn(new ImageStoragePort.ImageUploadResult(id.toString(), imageUrl));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = uploadProductImageUseCase.execute(id, file);

        assertThat(result.imageUrl()).isEqualTo(imageUrl);
        verify(imageStoragePort).upload(any(), eq("tomato.jpg"), eq("huerto/categories"));
        verify(productRepository).save(any());
    }

    @Test
    void should_throw_when_product_not_found() {
        UUID id = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file", "tomato.jpg", MediaType.IMAGE_JPEG_VALUE, "image-bytes".getBytes()
        );

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> uploadProductImageUseCase.execute(id, file))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(imageStoragePort, never()).upload(any(), any(), any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void should_replace_existing_image_and_delete_old_one() {
        UUID id = UUID.randomUUID();
        String oldUrl = "https://res.cloudinary.com/huerto/image/upload/huerto/categories/old.jpg";
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product = new Product(id, "Tomato", variety, Price.of("2.50"),
                Unit.KG, 100, true, oldUrl, 0);

        String newUrl = "https://res.cloudinary.com/huerto/image/upload/huerto/categories/new.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file", "new.jpg", MediaType.IMAGE_JPEG_VALUE, "new-bytes".getBytes()
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(imageStoragePort.upload(any(), eq("new.jpg"), eq("huerto/categories")))
                .thenReturn(new ImageStoragePort.ImageUploadResult(id.toString(), newUrl));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = uploadProductImageUseCase.execute(id, file);

        assertThat(result.imageUrl()).isEqualTo(newUrl);
        verify(imageStoragePort).delete(id.toString());
        verify(productRepository).save(any());
    }
}