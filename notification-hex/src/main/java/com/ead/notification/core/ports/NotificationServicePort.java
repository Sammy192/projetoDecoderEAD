package com.ead.notification.core.ports;

import com.ead.notification.core.domain.NotificationDomain;
import com.ead.notification.core.domain.PageInfo;
import com.ead.notification.core.domain.enums.NotificationStatus;

import java.util.List;
import java.util.UUID;

public interface NotificationServicePort {

    NotificationDomain saveNotification(NotificationDomain notificationDomain);

    List<NotificationDomain> findAllNotificationsCreatedByUser(UUID userId, PageInfo pageInfo);

    NotificationDomain updateNotificationStatus(UUID userId, UUID notificationId, NotificationStatus notificationStatus);
}
