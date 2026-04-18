package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.usecase.notification.GetNotificationHistoryUseCase;
import com.huerto.api.application.usecase.notification.SendManualNotificationUseCase;
import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.infrastructure.adapters.in.web.dto.NotificationResponse;
import com.huerto.api.infrastructure.adapters.in.web.dto.SendManualNotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/notifications")
public class AdminNotificationController {

    private final SendManualNotificationUseCase sendManualNotificationUseCase;
    private final GetNotificationHistoryUseCase getNotificationHistoryUseCase;

    public AdminNotificationController(SendManualNotificationUseCase sendManualNotificationUseCase,
                                       GetNotificationHistoryUseCase getNotificationHistoryUseCase) {
        this.sendManualNotificationUseCase = sendManualNotificationUseCase;
        this.getNotificationHistoryUseCase = getNotificationHistoryUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SendManualNotificationResponse send(
            @RequestParam("customerIds") List<String> customerIds,
            @RequestParam("messageText") String messageText,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        List<UUID> customerUUIDs = customerIds.stream()
                .map(UUID::fromString)
                .toList();

        byte[] imageBytes = file != null ? file.getBytes() : null;
        String mimeType = file != null ? file.getContentType() : null;
        String filename = file != null ? file.getOriginalFilename() : null;

        return sendManualNotificationUseCase.execute(
                customerUUIDs, messageText, imageBytes, mimeType, filename);
    }

    @GetMapping
    public Page<NotificationResponse> history(
            @RequestParam DeliveryStatus status,
            Pageable pageable) {
        return getNotificationHistoryUseCase.execute(status, pageable)
                .map(NotificationResponse::from);
    }
}