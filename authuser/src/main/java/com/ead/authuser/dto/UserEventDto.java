package com.ead.authuser.dto;

import com.ead.authuser.enums.ActionType;
import com.ead.authuser.models.UserModel;

import java.util.UUID;

public class UserEventDto {

    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private String userStatus;
    private String userType;
    private String phoneNumber;
    private String imageUrl;
    private String actionType;

    public UserEventDto(UUID userId, String username, String email, String fullName, String userStatus, String userType, String phoneNumber, String imageUrl, String actionType) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.userStatus = userStatus;
        this.userType = userType;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.actionType = actionType;
    }

    public UserEventDto(UserModel userModel, ActionType actionType) {
        this.userId = userModel.getUserId();
        this.username = userModel.getUsername();
        this.email = userModel.getEmail();
        this.fullName = userModel.getFullName();
        this.userStatus = userModel.getUserStatus().toString();
        this.userType = userModel.getUserType().toString();
        this.phoneNumber = userModel.getPhoneNumber();
        this.imageUrl = userModel.getImageUrl();
        this.actionType = actionType.toString();
    }

    public UserEventDto() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
