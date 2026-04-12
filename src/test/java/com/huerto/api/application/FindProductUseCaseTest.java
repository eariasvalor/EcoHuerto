package com.huerto.api.application;

import com.huerto.api.application.impl.product.FindProductUseCaseImpl;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.valueobject.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindProductUseCaseTest {

    @Mock ProductRepository productRepository;
    @InjectMocks FindProductUseCaseImpl findProductUseCase;

    @Test
    void should_return_product_when_found() {
        UUID id = UUID.randomUUID();
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product = new Product(
                id, "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, null, 0
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        Product result = findProductUseCase.execute(id);

        assertThat(result).isEqualTo(product);
        verify(productRepository).findById(id);
    }

    @Test
    void should_throw_when_product_not_found() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findProductUseCase.execute(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}