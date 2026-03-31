package com.huerto.api.application;

import com.huerto.api.application.impl.product.ListProductsUseCaseImpl;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.valueobject.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListProductsUseCaseTest {

    @Mock ProductRepository productRepository;
    @InjectMocks ListProductsUseCaseImpl listProductsUseCase;

    @Test
    void should_return_only_available_products() {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato");
        Product available = new Product(
                UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, 0
        );

        when(productRepository.findAllAvailable()).thenReturn(List.of(available));

        List<Product> result = listProductsUseCase.execute();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).available()).isTrue();
        verify(productRepository).findAllAvailable();
        verify(productRepository, never()).findAll();
    }

    @Test
    void should_return_empty_list_when_no_available_products() {
        when(productRepository.findAllAvailable()).thenReturn(List.of());

        List<Product> result = listProductsUseCase.execute();

        assertThat(result).isEmpty();
    }
}