package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.usecase.notification.UploadNotificationMediaUseCase;
import com.huerto.api.domain.model.MediaUploadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/media")
public class AdminMediaController {

    private static final List<String> ALLOWED_MIME_TYPES =
            List.of("image/jpeg", "image/png");

    @Value("${notification.media.max-size-bytes:5242880}")
    private long maxSizeBytes;

    private final UploadNotificationMediaUseCase uploadNotificationMediaUseCase;

    public AdminMediaController(UploadNotificationMediaUseCase uploadNotificationMediaUseCase) {
        this.uploadNotificationMediaUseCase = uploadNotificationMediaUseCase;
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('OWNER', 'ASSISTANT')")
    public MediaUploadResult upload(@RequestParam("file") MultipartFile file)
            throws IOException {

        if (file.getSize() > maxSizeBytes)
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                    "File exceeds maximum allowed size of " + maxSizeBytes + " bytes");

        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Only image/jpeg and image/png are accepted");

        return uploadNotificationMediaUseCase.execute(
                file.getBytes(),
                mimeType,
                file.getOriginalFilename()
        );
    }
}