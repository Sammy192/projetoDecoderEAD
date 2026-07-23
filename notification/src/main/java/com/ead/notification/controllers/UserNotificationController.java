package com.ead.notification.controllers;

import com.ead.notification.dtos.NotificationStatusDTO;
import com.ead.notification.models.NotificationModel;
import com.ead.notification.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserNotificationController {

    private final NotificationService notificationService;

    public UserNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<Page<NotificationModel>> findAllNotificationsCreatedByUser(@PathVariable(value = "userId") UUID userId,
                                                                           Pageable pageable) {
        return ResponseEntity.ok(notificationService.findAllNotificationsCreatedByUser(userId, pageable));
    }

    @PutMapping("/users/{userId}/notifications/{notificationId}/status")
    public ResponseEntity<NotificationModel> updateNotificationStatus(@PathVariable(value = "userId") UUID userId,
                                                           @PathVariable(value = "notificationId") UUID notificationId,
                                                           @RequestBody @Valid NotificationStatusDTO notificationStatusDTO) {

        NotificationModel notificationModel = notificationService.updateNotificationStatus(userId, notificationId, notificationStatusDTO);

        return ResponseEntity.ok(notificationModel);
    }
}
