package com.huerto.api.application;

import com.huerto.api.application.impl.product.DeleteProductImageUseCaseImpl;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ImageStoragePort;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.valueobject.Description;
import com.huerto.api.domain.valueobject.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductImageUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock ImageStoragePort imageStoragePort;
    @InjectMocks DeleteProductImageUseCaseImpl deleteProductImageUseCase;

    private Product buildProduct(UUID id, String imageUrl) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf",  "Tomato", null);
        return new Product(id, "Tomato", new Description("Fresh tomato"), variety, Price.of("2.50"),
                Unit.KG, 100, true, imageUrl, 0);
    }

    @Test
    void should_delete_image_and_clear_url() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(id, "https://res.cloudinary.com/huerto/categories/abc.jpg");

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        deleteProductImageUseCase.execute(id);

        verify(imageStoragePort).delete(id.toString());
        verify(productRepository).save(argThat(p -> p.imageUrl() == null));
    }

    @Test
    void should_throw_when_product_not_found() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProductImageUseCase.execute(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(imageStoragePort, never()).delete(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void should_do_nothing_on_cloudinary_when_product_has_no_image() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(id, null);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        deleteProductImageUseCase.execute(id);

        verify(imageStoragePort, never()).delete(any());
        verify(productRepository).save(argThat(p -> p.imageUrl() == null));
    }
}