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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(available), pageable, 1);

        when(productRepository.findAllAvailable(pageable)).thenReturn(page);

        Page<Product> result = listProductsUseCase.execute(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).available()).isTrue();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(productRepository).findAllAvailable(pageable);
        verify(productRepository, never()).findAll();
    }

    @Test
    void should_return_empty_page_when_no_available_products() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> emptyPage = Page.empty(pageable);

        when(productRepository.findAllAvailable(pageable)).thenReturn(emptyPage);

        Page<Product> result = listProductsUseCase.execute(pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }
}