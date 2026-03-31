package com.huerto.api.application;

import com.huerto.api.application.commands.CreateProductCommand;
import com.huerto.api.application.impl.product.CreateProductUseCaseImpl;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.ports.out.VarietyRepository;
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
class CreateProductUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock VarietyRepository varietyRepository;

    @InjectMocks CreateProductUseCaseImpl createProductUseCase;

    @Test
    void should_create_product_when_variety_exists() {
        UUID varietyId = UUID.randomUUID();
        Variety variety = new Variety(varietyId, "Raf", "Tomato");
        CreateProductCommand command = new CreateProductCommand(
                "Tomato", varietyId, new BigDecimal("2.50"), Unit.KG, 100
        );

        when(varietyRepository.findById(varietyId)).thenReturn(Optional.of(variety));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = createProductUseCase.execute(command);

        assertThat(result.name()).isEqualTo("Tomato");
        assertThat(result.variety()).isEqualTo(variety);
        assertThat(result.stock()).isEqualTo(100);
        assertThat(result.available()).isTrue();
        verify(productRepository).save(any());
    }

    @Test
    void should_throw_when_variety_does_not_exist() {
        UUID varietyId = UUID.randomUUID();
        CreateProductCommand command = new CreateProductCommand(
                "Tomato", varietyId, new BigDecimal("2.50"), Unit.KG, 100
        );

        when(varietyRepository.findById(varietyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createProductUseCase.execute(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(varietyId.toString());

        verify(productRepository, never()).save(any());
    }
}
