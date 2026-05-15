package com.ead.course.dto;

import com.ead.course.enums.UserStatusEnum;
import com.ead.course.enums.UserTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(UUID userId,
                      String username,
                      String email,
                      String fullName,
                      UserStatusEnum userStatus,
                      UserTypeEnum userType,
                      String phoneNumber,
                      String imageUrl) {
}
