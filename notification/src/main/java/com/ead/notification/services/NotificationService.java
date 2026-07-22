package com.ead.notification.services;

import com.ead.notification.dtos.NotificationCommandDTO;

public interface NotificationService {
    void saveNotification(NotificationCommandDTO notificationCommandDTO);
}
