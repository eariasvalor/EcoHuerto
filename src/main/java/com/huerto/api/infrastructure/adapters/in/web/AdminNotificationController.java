package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.usecase.notification.GetNotificationHistoryUseCase;
import com.huerto.api.application.usecase.notification.SendManualNotificationUseCase;
import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.infrastructure.adapters.in.web.dto.NotificationResponse;
import com.huerto.api.infrastructure.adapters.in.web.dto.SendManualNotificationRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.SendManualNotificationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public Page<NotificationResponse> history(
            @RequestParam DeliveryStatus status,
            Pageable pageable) {
        return getNotificationHistoryUseCase.execute(status, pageable)
                .map(NotificationResponse::from);
    }
}