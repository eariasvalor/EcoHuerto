package com.huerto.api.application;

import com.huerto.api.application.impl.variety.UploadVarietyImageUseCaseImpl;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ImageStoragePort;
import com.huerto.api.domain.ports.out.VarietyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadVarietyImageUseCaseTest {

    @Mock VarietyRepository varietyRepository;
    @Mock ImageStoragePort imageStoragePort;
    @InjectMocks UploadVarietyImageUseCaseImpl uploadVarietyImageUseCase;

    private Variety buildVariety(UUID id, String imageUrl) {
        return new Variety(id, "Raf", "Tomato", imageUrl);
    }

    @Test
    void should_upload_image_and_persist_url() {
        UUID id = UUID.randomUUID();
        Variety variety = buildVariety(id, null);
        String imageUrl = "https://res.cloudinary.com/huerto/image/upload/huerto/varieties/abc.jpg";

        MockMultipartFile file = new MockMultipartFile(
                "file", "raf.jpg", MediaType.IMAGE_JPEG_VALUE, "image-bytes".getBytes()
        );

        when(varietyRepository.findById(id)).thenReturn(Optional.of(variety));
        when(imageStoragePort.upload(any(), eq("raf.jpg"), eq("huerto/varieties")))
                .thenReturn(new ImageStoragePort.ImageUploadResult(id.toString(), imageUrl));
        when(varietyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Variety result = uploadVarietyImageUseCase.execute(id, file);

        assertThat(result.imageUrl()).isEqualTo(imageUrl);
        verify(imageStoragePort).upload(any(), eq("raf.jpg"), eq("huerto/varieties"));
        verify(varietyRepository).save(any());
    }

    @Test
    void should_throw_when_variety_not_found() {
        UUID id = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file", "raf.jpg", MediaType.IMAGE_JPEG_VALUE, "image-bytes".getBytes()
        );

        when(varietyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> uploadVarietyImageUseCase.execute(id, file))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(imageStoragePort, never()).upload(any(), any(), any());
        verify(varietyRepository, never()).save(any());
    }

    @Test
    void should_replace_existing_image_and_delete_old_one() {
        UUID id = UUID.randomUUID();
        String oldUrl = "https://res.cloudinary.com/huerto/image/upload/huerto/varieties/old.jpg";
        Variety variety = buildVariety(id, oldUrl);

        String newUrl = "https://res.cloudinary.com/huerto/image/upload/huerto/varieties/new.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file", "new.jpg", MediaType.IMAGE_JPEG_VALUE, "new-bytes".getBytes()
        );

        when(varietyRepository.findById(id)).thenReturn(Optional.of(variety));
        when(imageStoragePort.upload(any(), eq("new.jpg"), eq("huerto/varieties")))
                .thenReturn(new ImageStoragePort.ImageUploadResult(id.toString(), newUrl));
        when(varietyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Variety result = uploadVarietyImageUseCase.execute(id, file);

        assertThat(result.imageUrl()).isEqualTo(newUrl);
        verify(imageStoragePort).delete(id.toString());
        verify(varietyRepository).save(any());
    }
}
