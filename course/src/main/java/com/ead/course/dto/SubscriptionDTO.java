package com.ead.course.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubscriptionDTO(@NotNull(message = "UserId is mandatory")
                              UUID userId) {
}
