package com.ead.notification.adapters.dtos;

import java.util.UUID;

public record NotificationCommandDTO(String title,
                                     String message,
                                     UUID userId) {
}
