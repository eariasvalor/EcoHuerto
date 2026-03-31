package com.huerto.api.application;

import com.huerto.api.application.impl.variety.ListVarietiesUseCaseImpl;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.VarietyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListVarietiesUseCaseTest {

    @Mock VarietyRepository varietyRepository;
    @InjectMocks ListVarietiesUseCaseImpl listVarietiesUseCase;

    @Test
    void should_return_paginated_varieties() {
        Pageable pageable = PageRequest.of(0, 10);
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato");
        Page<Variety> page = new PageImpl<>(List.of(variety), pageable, 1);

        when(varietyRepository.findAll(pageable)).thenReturn(page);

        Page<Variety> result = listVarietiesUseCase.execute(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(varietyRepository).findAll(pageable);
    }

    @Test
    void should_return_empty_page_when_no_varieties() {
        Pageable pageable = PageRequest.of(0, 10);

        when(varietyRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<Variety> result = listVarietiesUseCase.execute(pageable);

        assertThat(result.getContent()).isEmpty();
    }
}