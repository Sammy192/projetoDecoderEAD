package com.ead.notification.adapters.inbounds.controllers;

import com.ead.notification.adapters.configs.security.AuthenticationCurrentUserService;
import com.ead.notification.adapters.configs.security.UserDetailsImpl;
import com.ead.notification.adapters.dtos.NotificationStatusDTO;
import com.ead.notification.core.domain.NotificationDomain;
import com.ead.notification.core.domain.PageInfo;
import com.ead.notification.core.ports.NotificationServicePort;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class UserNotificationController {

    private final NotificationServicePort notificationServicePort;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;


    public UserNotificationController(NotificationServicePort notificationServicePort, AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.notificationServicePort = notificationServicePort;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
    }


    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<Page<NotificationDomain>> findAllNotificationsCreatedByUser(@PathVariable(value = "userId") UUID userId,
                                                                                     Pageable pageable) {
        UserDetailsImpl userDetails = authenticationCurrentUserService.getCurrentUser();
        if(userDetails.getUserId().equals(userId) || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            PageInfo pageInfo = new PageInfo();
            BeanUtils.copyProperties(pageable, pageInfo);
            List<NotificationDomain> notificationDomainList = notificationServicePort.findAllNotificationsCreatedByUser(userId, pageInfo);
            PageImpl<NotificationDomain> notificationDomainPage = new PageImpl<>(notificationDomainList, pageable, notificationDomainList.size());
            return ResponseEntity.ok(notificationDomainPage);
        } else {
            throw  new AccessDeniedException("Forbidden");
        }
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PutMapping("/users/{userId}/notifications/{notificationId}/status")
    public ResponseEntity<NotificationDomain> updateNotificationStatus(@PathVariable(value = "userId") UUID userId,
                                                                      @PathVariable(value = "notificationId") UUID notificationId,
                                                                      @RequestBody @Valid NotificationStatusDTO notificationStatusDTO) {
        UserDetailsImpl userDetails = authenticationCurrentUserService.getCurrentUser();
        if(userDetails.getUserId().equals(userId) || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            NotificationDomain notificationDomain = notificationServicePort.updateNotificationStatus(userId, notificationId, notificationStatusDTO.notificationStatus());
            return ResponseEntity.ok(notificationDomain);
        } else {
            throw  new AccessDeniedException("Forbidden");
        }
    }
}
