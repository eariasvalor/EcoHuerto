package com.huerto.api.application;

import com.huerto.api.application.impl.variety.DeleteVarietyImageUseCaseImpl;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ImageStoragePort;
import com.huerto.api.domain.ports.out.VarietyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteVarietyImageUseCaseTest {

    @Mock VarietyRepository varietyRepository;
    @Mock ImageStoragePort imageStoragePort;
    @InjectMocks DeleteVarietyImageUseCaseImpl deleteVarietyImageUseCase;

    private Variety buildVariety(UUID id, String imageUrl) {
        return new Variety(id, "Raf", "Tomato", imageUrl);
    }

    @Test
    void should_delete_image_and_clear_url() {
        UUID id = UUID.randomUUID();
        Variety variety = buildVariety(id, "https://res.cloudinary.com/huerto/varieties/abc.jpg");

        when(varietyRepository.findById(id)).thenReturn(Optional.of(variety));
        when(varietyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        deleteVarietyImageUseCase.execute(id);

        verify(imageStoragePort).delete(id.toString());
        verify(varietyRepository).save(argThat(v -> v.imageUrl() == null));
    }

    @Test
    void should_throw_when_variety_not_found() {
        UUID id = UUID.randomUUID();

        when(varietyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteVarietyImageUseCase.execute(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(imageStoragePort, never()).delete(any());
        verify(varietyRepository, never()).save(any());
    }

    @Test
    void should_do_nothing_on_cloudinary_when_variety_has_no_image() {
        UUID id = UUID.randomUUID();
        Variety variety = buildVariety(id, null);

        when(varietyRepository.findById(id)).thenReturn(Optional.of(variety));
        when(varietyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        deleteVarietyImageUseCase.execute(id);

        verify(imageStoragePort, never()).delete(any());
        verify(varietyRepository).save(argThat(v -> v.imageUrl() == null));
    }
}