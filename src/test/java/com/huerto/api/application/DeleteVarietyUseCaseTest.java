package com.huerto.api.application;

import com.huerto.api.application.impl.variety.DeleteVarietyUseCaseImpl;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.exception.VarietyInUseException;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.ports.out.VarietyRepository;
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
class DeleteVarietyUseCaseTest {

    @Mock VarietyRepository varietyRepository;
    @Mock ProductRepository productRepository;
    @InjectMocks DeleteVarietyUseCaseImpl deleteVarietyUseCase;

    @Test
    void should_delete_variety_when_not_in_use() {
        UUID id = UUID.randomUUID();

        when(varietyRepository.existsById(id)).thenReturn(true);
        when(productRepository.existsByVarietyId(id)).thenReturn(false);

        deleteVarietyUseCase.execute(id);

        verify(varietyRepository).deleteById(id);
    }

    @Test
    void should_throw_when_variety_not_found() {
        UUID id = UUID.randomUUID();

        when(varietyRepository.existsById(id)).thenReturn(false);

        ThrowingCallable execute = () -> deleteVarietyUseCase.execute(id);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
        verify(varietyRepository, never()).deleteById(any());
    }

    @Test
    void should_throw_when_variety_is_in_use() {
        UUID id = UUID.randomUUID();

        when(varietyRepository.existsById(id)).thenReturn(true);
        when(productRepository.existsByVarietyId(id)).thenReturn(true);

        ThrowingCallable execute = () -> deleteVarietyUseCase.execute(id);

        assertThatThrownBy(execute).isInstanceOf(VarietyInUseException.class);
        verify(varietyRepository, never()).deleteById(any());
    }
}