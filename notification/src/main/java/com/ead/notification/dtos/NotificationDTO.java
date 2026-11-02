package com.ead.notification.dtos;

import com.ead.notification.enums.NotificationStatus;
import com.ead.notification.models.NotificationModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDTO(
        UUID notificationId,
        UUID userId,
        String title,
        String message,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime creationDate,
        NotificationStatus notificationStatus
) {
    public NotificationDTO(NotificationModel model) {
        this(model.getNotificationId(),
                model.getUserId(),
                model.getTitle(),
                model.getMessage(),
                model.getCreationDate(),
                model.getNotificationStatus());
    }
}