package com.ead.notification.core.services;

import com.ead.notification.adapters.exceptions.NotFoundException;
import com.ead.notification.core.domain.NotificationDomain;
import com.ead.notification.core.domain.PageInfo;
import com.ead.notification.core.domain.enums.NotificationStatus;
import com.ead.notification.core.ports.NotificationPersistencePort;
import com.ead.notification.core.ports.NotificationServicePort;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public class NotificationServicePortImpl implements NotificationServicePort {

    private final NotificationPersistencePort notificationPersistencePort;

    public NotificationServicePortImpl(NotificationPersistencePort notificationPersistencePort) {
        this.notificationPersistencePort = notificationPersistencePort;
    }

    @Override
    public NotificationDomain saveNotification(NotificationDomain notificationDomain) {
        notificationDomain.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        notificationDomain.setNotificationStatus(NotificationStatus.CREATED);
        return notificationPersistencePort.saveNotification(notificationDomain);
    }

    @Override
    public List<NotificationDomain> findAllNotificationsCreatedByUser(UUID userId, PageInfo pageInfo) {

        return notificationPersistencePort.findAllByUserIdAndNotificationStatus(userId, NotificationStatus.CREATED, pageInfo);
    }

    @Override
    public NotificationDomain updateNotificationStatus(UUID userId, UUID notificationId, NotificationStatus notificationStatus) {
        NotificationDomain notificationDomain = findNotificationByUserIdAndNotificationId(userId, notificationId);

        notificationDomain.setNotificationStatus(notificationStatus);
        return notificationPersistencePort.saveNotification(notificationDomain);
    }

    private NotificationDomain findNotificationByUserIdAndNotificationId(UUID userId, UUID notificationId) {
        return notificationPersistencePort.findByUserIdAndNotificationId(userId, notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found!"));
    }
}
