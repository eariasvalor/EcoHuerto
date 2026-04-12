package com.huerto.api.infrastructure;

import com.huerto.api.application.usecase.variety.UploadVarietyImageUseCase;
import com.huerto.api.application.usecase.variety.DeleteVarietyImageUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.infrastructure.adapters.in.web.AdminVarietyController;
import com.huerto.api.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AdminVarietyController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
)
class AdminVarietyControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean UploadVarietyImageUseCase uploadVarietyImageUseCase;
    @MockBean DeleteVarietyImageUseCase deleteVarietyImageUseCase;


    private Variety buildVariety(UUID id, String imageUrl) {
        return new Variety(id, "Raf", "Tomato", imageUrl);
    }

    @Test
    void should_return_200_when_image_is_uploaded() throws Exception {
        UUID varietyId = UUID.randomUUID();
        String imageUrl = "https://res.cloudinary.com/huerto/image/upload/huerto/varieties/abc.jpg";
        Variety updated = buildVariety(varietyId, imageUrl);

        MockMultipartFile file = new MockMultipartFile(
                "file", "raf.jpg", MediaType.IMAGE_JPEG_VALUE, "image-bytes".getBytes()
        );

        when(uploadVarietyImageUseCase.execute(eq(varietyId), any())).thenReturn(updated);

        mockMvc.perform(multipart("/api/v1/admin/varieties/{id}/image", varietyId)
                        .file(file)
                        .with(req -> { req.setMethod("PATCH"); return req; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value(imageUrl));
    }

    @Test
    void should_return_404_when_variety_not_found_on_image_upload() throws Exception {
        UUID varietyId = UUID.randomUUID();

        MockMultipartFile file = new MockMultipartFile(
                "file", "raf.jpg", MediaType.IMAGE_JPEG_VALUE, "image-bytes".getBytes()
        );

        when(uploadVarietyImageUseCase.execute(eq(varietyId), any()))
                .thenThrow(new ResourceNotFoundException("Variety", varietyId));

        mockMvc.perform(multipart("/api/v1/admin/varieties/{id}/image", varietyId)
                        .file(file)
                        .with(req -> { req.setMethod("PATCH"); return req; }))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_400_when_file_is_empty() throws Exception {
        UUID varietyId = UUID.randomUUID();

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "raf.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]
        );

        mockMvc.perform(multipart("/api/v1/admin/varieties/{id}/image", varietyId)
                        .file(emptyFile)
                        .with(req -> { req.setMethod("PATCH"); return req; }))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_204_when_image_is_deleted() throws Exception {
        UUID varietyId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/admin/varieties/{id}/image", varietyId))
                .andExpect(status().isNoContent());

        verify(deleteVarietyImageUseCase).execute(varietyId);
    }

    @Test
    void should_return_404_when_variety_not_found_on_image_delete() throws Exception {
        UUID varietyId = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("Variety", varietyId))
                .when(deleteVarietyImageUseCase).execute(varietyId);

        mockMvc.perform(delete("/api/v1/admin/varieties/{id}/image", varietyId))
                .andExpect(status().isNotFound());
    }
}