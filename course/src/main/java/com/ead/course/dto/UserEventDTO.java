package com.ead.course.dto;

import java.util.UUID;

public record UserEventDTO(UUID userId,
                           String username,
                           String email,
                           String fullName,
                           String userStatus,
                           String userType,
                           String phoneNumber,
                           String imageUrl,
                           String actionType) {
}
