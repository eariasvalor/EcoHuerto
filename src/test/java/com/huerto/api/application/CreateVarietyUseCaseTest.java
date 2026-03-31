package com.huerto.api.application;

import com.huerto.api.application.commands.CreateVarietyCommand;
import com.huerto.api.application.impl.variety.CreateVarietyUseCaseImpl;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.VarietyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateVarietyUseCaseTest {

    @Mock VarietyRepository varietyRepository;
    @InjectMocks CreateVarietyUseCaseImpl createVarietyUseCase;

    @Test
    void should_create_variety() {
        CreateVarietyCommand command = new CreateVarietyCommand("Raf", "Tomato");

        when(varietyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Variety result = createVarietyUseCase.execute(command);

        assertThat(result.name()).isEqualTo("Raf");
        assertThat(result.productCategory()).isEqualTo("Tomato");
        assertThat(result.id()).isNotNull();
        verify(varietyRepository).save(any());
    }
}