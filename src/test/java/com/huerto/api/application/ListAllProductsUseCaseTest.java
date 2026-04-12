package com.huerto.api.application;

import com.huerto.api.application.impl.product.ListAllProductsUseCaseImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListAllProductsUseCaseTest {

    @Mock ProductRepository productRepository;
    @InjectMocks ListAllProductsUseCaseImpl listAllProductsUseCase;

    @Test
    void should_return_all_products_including_unavailable() {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product available = new Product(
                UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, null,0
        );
        Product unavailable = new Product(
                UUID.randomUUID(), "Cherry Tomato", variety,
                Price.of("3.00"), Unit.KG, 0, false, null, 0
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(available, unavailable), pageable, 2);

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<Product> result = listAllProductsUseCase.execute(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).available()).isTrue();
        assertThat(result.getContent().get(1).available()).isFalse();
        verify(productRepository).findAll(pageable);
        verify(productRepository, never()).findAllAvailable(any());
    }

    @Test
    void should_return_empty_page_when_no_products() {
        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<Product> result = listAllProductsUseCase.execute(pageable);

        assertThat(result.getContent()).isEmpty();
    }
}