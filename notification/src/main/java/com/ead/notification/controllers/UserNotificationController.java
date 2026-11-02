package com.ead.notification.controllers;

import com.ead.notification.configs.security.AuthenticationCurrentUserService;
import com.ead.notification.configs.security.UserDetailsImpl;
import com.ead.notification.dtos.NotificationDTO;
import com.ead.notification.dtos.NotificationStatusDTO;
import com.ead.notification.models.NotificationModel;
import com.ead.notification.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserNotificationController {

    private final NotificationService notificationService;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;

    public UserNotificationController(NotificationService notificationService, AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.notificationService = notificationService;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<Page<NotificationDTO>> findAllNotificationsCreatedByUser(@PathVariable(value = "userId") UUID userId,
                                                                           Pageable pageable) {
        UserDetailsImpl userDetails = authenticationCurrentUserService.getCurrentUser();
        if(userDetails.getUserId().equals(userId) || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            Page<NotificationModel> notificationModelPage = notificationService.findAllNotificationsCreatedByUser(userId, pageable);
            return ResponseEntity.ok(notificationModelPage.map(NotificationDTO::new));
        } else {
            throw  new AccessDeniedException("Forbidden");
        }
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PutMapping("/users/{userId}/notifications/{notificationId}/status")
    public ResponseEntity<NotificationDTO> updateNotificationStatus(@PathVariable(value = "userId") UUID userId,
                                                           @PathVariable(value = "notificationId") UUID notificationId,
                                                           @RequestBody @Valid NotificationStatusDTO notificationStatusDTO) {
        UserDetailsImpl userDetails = authenticationCurrentUserService.getCurrentUser();
        if(userDetails.getUserId().equals(userId) || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            NotificationModel notificationModel = notificationService.updateNotificationStatus(userId, notificationId, notificationStatusDTO);
            return ResponseEntity.ok(new NotificationDTO(notificationModel));
        } else {
            throw  new AccessDeniedException("Forbidden");
        }
    }
}
