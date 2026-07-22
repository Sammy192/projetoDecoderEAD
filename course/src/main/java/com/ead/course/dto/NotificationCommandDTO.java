package com.ead.course.dto;

import java.util.UUID;

public record NotificationCommandDTO(String title,
                                     String message,
                                     UUID userId) {
}
