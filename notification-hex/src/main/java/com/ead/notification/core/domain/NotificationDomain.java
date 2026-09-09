package com.ead.notification.core.domain;

import com.ead.notification.core.domain.enums.NotificationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationDomain {

    private UUID notificationId;
    private UUID userId;
    private String title;
    private String message;
    private LocalDateTime creationDate;
    private NotificationStatus notificationStatus;

    public NotificationDomain(UUID notificationId, UUID userId, String title, String message, LocalDateTime creationDate, NotificationStatus notificationStatus) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.creationDate = creationDate;
        this.notificationStatus = notificationStatus;
    }

    public NotificationDomain() {
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
}
