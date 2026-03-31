package com.huerto.api.application;

import com.huerto.api.application.impl.product.DeleteProductUseCaseImpl;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.ports.out.ProductRepository;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock ProductRepository productRepository;
    @InjectMocks DeleteProductUseCaseImpl deleteProductUseCase;

    @Test
    void should_delete_product_when_exists() {
        UUID id = UUID.randomUUID();

        when(productRepository.existsById(id)).thenReturn(true);

        deleteProductUseCase.execute(id);

        verify(productRepository).deleteById(id);
    }

    @Test
    void should_throw_when_product_not_found() {
        UUID id = UUID.randomUUID();

        when(productRepository.existsById(id)).thenReturn(false);

        ThrowingCallable execute = () -> deleteProductUseCase.execute(id);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
        verify(productRepository, never()).deleteById(any());
    }
}