package com.ead.notification.services.impl;

import com.ead.notification.configs.exceptions.NotFoundException;
import com.ead.notification.dtos.NotificationCommandDTO;
import com.ead.notification.dtos.NotificationStatusDTO;
import com.ead.notification.enums.NotificationStatus;
import com.ead.notification.models.NotificationModel;
import com.ead.notification.repositories.NotificationRepository;
import com.ead.notification.services.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void saveNotification(NotificationCommandDTO notificationCommandDTO) {
        NotificationModel notificationModel = new NotificationModel();
        BeanUtils.copyProperties(notificationCommandDTO, notificationModel);
        notificationModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        notificationModel.setNotificationStatus(NotificationStatus.CREATED);
        notificationRepository.save(notificationModel);
    }

    @Override
    public Page<NotificationModel> findAllNotificationsCreatedByUser(UUID userId, Pageable pageable) {
        return notificationRepository.findAllByUserIdAndNotificationStatus(userId, NotificationStatus.CREATED, pageable);
    }

    @Override
    public NotificationModel updateNotificationStatus(UUID userId, UUID notificationId, NotificationStatusDTO notificationStatusDTO) {
        NotificationModel notificationModel = findNotificationByUserIdAndNotificationId(userId, notificationId);

        notificationModel.setNotificationStatus(notificationStatusDTO.notificationStatus());
        return notificationRepository.save(notificationModel);
    }

    private NotificationModel findNotificationByUserIdAndNotificationId(UUID userId, UUID notificationId) {
        return notificationRepository.findByUserIdAndNotificationId(userId, notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found!"));
    }
}
