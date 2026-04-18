package com.huerto.api.infrastructure;

import com.huerto.api.application.usecase.notification.UploadNotificationMediaUseCase;
import com.huerto.api.domain.model.MediaUploadResult;
import com.huerto.api.infrastructure.adapters.in.web.AdminMediaController;
import com.huerto.api.infrastructure.config.JwtAuthFilter;
import com.huerto.api.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminMediaController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminMediaControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean UploadNotificationMediaUseCase uploadNotificationMediaUseCase;

    @Test
    void should_upload_image_and_return_media_result() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "image-bytes".getBytes());

        when(uploadNotificationMediaUseCase.execute(any(), eq("image/jpeg"), eq("photo.jpg")))
                .thenReturn(new MediaUploadResult("pub123", "https://cloudinary.com/photo.jpg"));

        mockMvc.perform(multipart("/api/v1/admin/media").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mediaId").value("pub123"))
                .andExpect(jsonPath("$.mediaUrl").value("https://cloudinary.com/photo.jpg"));
    }

    @Test
    void should_return_415_when_mime_type_not_allowed() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "pdf-bytes".getBytes());

        mockMvc.perform(multipart("/api/v1/admin/media").file(file))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void should_return_413_when_file_exceeds_max_size() throws Exception {
        byte[] largeFile = new byte[6 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file", "large.jpg", "image/jpeg", largeFile);

        mockMvc.perform(multipart("/api/v1/admin/media").file(file))
                .andExpect(status().isPayloadTooLarge());
    }
}