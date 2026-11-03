package com.ead.course.dto;

import com.ead.course.models.UserModel;

import java.util.UUID;

public record UserResponseDTO(
        UUID userId,
        String email,
        String fullName,
        String userStatus,
        String userType,
        String imageUrl
) {
    public UserResponseDTO(UserModel model) {
        this(
                model.getUserId(),
                model.getEmail(),
                model.getFullName(),
                model.getUserStatus(),
                model.getUserType(),
                model.getImageUrl()
        );
    }
}