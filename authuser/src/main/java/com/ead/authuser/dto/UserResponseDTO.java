package com.ead.authuser.dto;

import com.ead.authuser.enums.UserStatusEnum;
import com.ead.authuser.enums.UserTypeEnum;
import com.ead.authuser.models.UserModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO extends RepresentationModel<UserResponseDTO> {

    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private UserStatusEnum userStatus;
    private UserTypeEnum userType;

    public UserResponseDTO(UserModel userModel) {
        this.userId = userModel.getUserId();
        this.username = userModel.getUsername();
        this.email = userModel.getEmail();
        this.fullName = userModel.getFullName();
        this.userStatus = userModel.getUserStatus();
        this.userType = userModel.getUserType();
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public UserStatusEnum getUserStatus() { return userStatus; }
    public void setUserStatus(UserStatusEnum userStatus) { this.userStatus = userStatus; }
    public UserTypeEnum getUserType() { return userType; }
    public void setUserType(UserTypeEnum userType) { this.userType = userType; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserResponseDTO that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(fullName, that.fullName) && userStatus == that.userStatus && userType == that.userType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, username, email, fullName, userStatus, userType);
    }
}