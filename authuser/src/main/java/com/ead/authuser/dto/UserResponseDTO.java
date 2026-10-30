package com.ead.authuser.dto;

import com.ead.authuser.enums.UserStatusEnum;
import com.ead.authuser.enums.UserTypeEnum;
import com.ead.authuser.models.UserModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(UUID userId,
                              String username,
                              String email,
                              String fullName,
                              UserStatusEnum userStatus,
                              UserTypeEnum userType,
                              LocalDateTime creationDate,
                              LocalDateTime lastUpdateDate) {


    public UserResponseDTO(UserModel userModel) {
        this(userModel.getUserId(),
                userModel.getUsername(),
                userModel.getEmail(),
                userModel.getFullName(),
                userModel.getUserStatus(),
                userModel.getUserType(),
                userModel.getCreationDate(),
                userModel.getLastUpdateDate());
    }
}