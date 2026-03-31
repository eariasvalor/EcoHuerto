package com.huerto.api.infrastructure;

import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.valueobject.Price;
import com.huerto.api.infrastructure.adapters.out.persistence.adapter.ProductJpaAdapter;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.ProductEntity;
import com.huerto.api.infrastructure.adapters.out.persistence.mapper.ProductEntityMapper;
import com.huerto.api.infrastructure.adapters.out.persistence.repository.ProductJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductJpaAdapterTest {

    @Mock ProductJpaRepository productJpaRepository;
    @Mock ProductEntityMapper productEntityMapper;

    @InjectMocks ProductJpaAdapter productJpaAdapter;

    private Variety buildVariety() {
        return new Variety(UUID.randomUUID(), "Raf", "Tomato");
    }

    private Product buildProduct(Variety variety) {
        return new Product(
                UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, 0
        );
    }

    @Test
    void should_save_and_return_product() {
        Variety variety = buildVariety();
        Product product = buildProduct(variety);
        ProductEntity entity = new ProductEntity();

        when(productEntityMapper.toEntity(product)).thenReturn(entity);
        when(productJpaRepository.save(entity)).thenReturn(entity);
        when(productEntityMapper.toDomain(entity)).thenReturn(product);

        Product result = productJpaAdapter.save(product);

        assertThat(result).isEqualTo(product);
        verify(productJpaRepository).save(entity);
    }

    @Test
    void should_find_product_by_id() {
        Variety variety = buildVariety();
        Product product = buildProduct(variety);
        ProductEntity entity = new ProductEntity();
        UUID id = product.id();

        when(productJpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productEntityMapper.toDomain(entity)).thenReturn(product);

        Optional<Product> result = productJpaAdapter.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(product);
    }

    @Test
    void should_return_empty_when_product_not_found() {
        UUID id = UUID.randomUUID();
        when(productJpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Product> result = productJpaAdapter.findById(id);

        assertThat(result).isEmpty();
        verify(productEntityMapper, never()).toDomain(any());
    }

    @Test
    void should_return_only_available_products() {
        Variety variety = buildVariety();
        Product product = buildProduct(variety);
        ProductEntity entity = new ProductEntity();

        when(productJpaRepository.findByAvailableTrue()).thenReturn(List.of(entity));
        when(productEntityMapper.toDomain(entity)).thenReturn(product);

        List<Product> result = productJpaAdapter.findAllAvailable();

        assertThat(result).hasSize(1);
        verify(productJpaRepository).findByAvailableTrue();
    }

    @Test
    void should_return_empty_list_when_no_available_products() {
        when(productJpaRepository.findByAvailableTrue()).thenReturn(List.of());

        List<Product> result = productJpaAdapter.findAllAvailable();

        assertThat(result).isEmpty();
    }
}
