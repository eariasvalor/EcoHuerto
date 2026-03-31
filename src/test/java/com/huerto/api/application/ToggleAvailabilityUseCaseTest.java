package com.huerto.api.application;

import com.huerto.api.application.impl.product.ToggleAvailabilityUseCaseImpl;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.valueobject.Price;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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
class ToggleAvailabilityUseCaseTest {

    @Mock ProductRepository productRepository;
    @InjectMocks ToggleAvailabilityUseCaseImpl toggleAvailabilityUseCase;

    private Product buildProduct(UUID id, boolean available) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato");
        return new Product(id, "Tomato", variety, Price.of("2.50"), Unit.KG, 100, available, 0);
    }

    @Test
    void should_toggle_available_to_false_when_currently_true() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(id, true);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = toggleAvailabilityUseCase.execute(id);

        assertThat(result.available()).isFalse();
        verify(productRepository).save(any());
    }

    @Test
    void should_toggle_available_to_true_when_currently_false() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(id, false);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = toggleAvailabilityUseCase.execute(id);

        assertThat(result.available()).isTrue();
        verify(productRepository).save(any());
    }

    @Test
    void should_throw_when_product_not_found() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        ThrowingCallable execute = () -> toggleAvailabilityUseCase.execute(id);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
        verify(productRepository, never()).save(any());
    }
}