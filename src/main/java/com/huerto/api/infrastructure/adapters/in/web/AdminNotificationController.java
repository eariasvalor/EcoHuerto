package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.usecase.notification.SendManualNotificationUseCase;
import com.huerto.api.infrastructure.adapters.in.web.dto.SendManualNotificationRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.SendManualNotificationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/notifications")
public class AdminNotificationController {

    private final SendManualNotificationUseCase sendManualNotificationUseCase;

    public AdminNotificationController(SendManualNotificationUseCase sendManualNotificationUseCase) {
        this.sendManualNotificationUseCase = sendManualNotificationUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public SendManualNotificationResponse send(
            @Valid @RequestBody SendManualNotificationRequest request) {
        return sendManualNotificationUseCase.execute(
                request.customerIds(),
                request.messageText(),
                request.mediaUrl()
        );
    }
}