package com.ead.notification.services;

import com.ead.notification.dtos.NotificationCommandDTO;
import com.ead.notification.models.NotificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    void saveNotification(NotificationCommandDTO notificationCommandDTO);

    Page<NotificationModel> findAllNotificationsCreatedByUser(UUID userId, Pageable pageable);
}
