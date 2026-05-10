package com.huerto.api.application;

import com.huerto.api.application.commands.UpdateStockCommand;
import com.huerto.api.application.impl.product.UpdateStockUseCaseImpl;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.InsufficientStockException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.valueobject.Description;
import com.huerto.api.domain.valueobject.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateStockUseCaseTest {

    @Mock ProductRepository productRepository;
    @InjectMocks UpdateStockUseCaseImpl updateStockUseCase;

    private Product buildProduct(UUID id, int stock) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        return new Product(id, "Tomato", new Description("Fresh tomato"), variety, Price.of("2.50"), Unit.KG, stock, true, null, 0);
    }

    @Test
    void should_increase_stock_when_quantity_is_positive() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(id, 100);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = updateStockUseCase.execute(new UpdateStockCommand(id, 50));

        assertThat(result.stock()).isEqualTo(150);
        verify(productRepository).save(any());
    }

    @Test
    void should_decrease_stock_when_quantity_is_negative() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(id, 100);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = updateStockUseCase.execute(new UpdateStockCommand(id, -30));

        assertThat(result.stock()).isEqualTo(70);
        verify(productRepository).save(any());
    }

    @Test
    void should_throw_when_stock_goes_negative() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(id, 10);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        ThrowingCallable execute = () ->
                updateStockUseCase.execute(new UpdateStockCommand(id, -50));

        assertThatThrownBy(execute).isInstanceOf(InsufficientStockException.class);
        verify(productRepository, never()).save(any());
    }

    @Test
    void should_throw_when_product_not_found() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        ThrowingCallable execute = () ->
                updateStockUseCase.execute(new UpdateStockCommand(id, 10));

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
    }
}