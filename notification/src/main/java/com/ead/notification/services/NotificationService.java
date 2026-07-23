package com.ead.notification.services;

import com.ead.notification.dtos.NotificationCommandDTO;
import com.ead.notification.dtos.NotificationStatusDTO;
import com.ead.notification.models.NotificationModel;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    void saveNotification(NotificationCommandDTO notificationCommandDTO);

    Page<NotificationModel> findAllNotificationsCreatedByUser(UUID userId, Pageable pageable);

    NotificationModel updateNotificationStatus(UUID userId, UUID notificationId, NotificationStatusDTO notificationStatusDTO);
}
