package com.huerto.api.application;

import com.huerto.api.application.commands.UpdateProductCommand;
import com.huerto.api.application.impl.product.UpdateProductUseCaseImpl;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.ports.out.VarietyRepository;
import com.huerto.api.domain.valueobject.Description;
import com.huerto.api.domain.valueobject.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock VarietyRepository varietyRepository;
    @InjectMocks UpdateProductUseCaseImpl updateProductUseCase;

    private Product buildProduct(UUID id, int stock) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        return new Product(id, "Tomato",new Description("Fresh tomato"), variety, Price.of("2.50"), Unit.KG, stock, true, null, 0);
    }

    @Test
    void should_update_product_when_exists() {
        UUID id = UUID.randomUUID();
       Product existing = buildProduct(id, 100);

        UpdateProductCommand command = new UpdateProductCommand(
                id, "Updated Tomato",new Description("Fresh tomato").toString(),existing.variety().id(), new BigDecimal("3.00"), Unit.KG
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(varietyRepository.findById(existing.variety().id())).thenReturn(Optional.of(existing.variety()));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = updateProductUseCase.execute(command);

        assertThat(result.name()).isEqualTo("Updated Tomato");
        assertThat(result.price().amount()).isEqualByComparingTo("3.00");
        verify(productRepository).save(any());
        assertThat(result.imageUrl()).isNull();
    }

    @Test
    void should_throw_when_product_not_found() {
        UUID id = UUID.randomUUID();
        UpdateProductCommand command = new UpdateProductCommand(
                id, "Updated Tomato", new Description("Fresh tomato").toString(), UUID.randomUUID(), new BigDecimal("3.00"), Unit.KG
        );

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProductUseCase.execute(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(productRepository, never()).save(any());
    }

    @Test
    void should_throw_when_variety_not_found() {
        UUID id = UUID.randomUUID();
        UUID varietyId = UUID.randomUUID();
        Variety variety = new Variety(varietyId, "Raf", "Tomato", null);
        Product existing = new Product(
                id, "Tomato", new Description("Fresh tomato"), variety,
                Price.of("2.50"), Unit.KG, 100, true, null, 0
        );
        UpdateProductCommand command = new UpdateProductCommand(
                id, "Updated Tomato", new Description("Fresh tomato").toString(), varietyId, new BigDecimal("3.00"), Unit.KG
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(varietyRepository.findById(varietyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProductUseCase.execute(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(varietyId.toString());

        verify(productRepository, never()).save(any());
    }
}