package com.ead.course.converters;

import com.ead.course.dto.UserEventDTO;
import com.ead.course.models.UserModel;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserModel convertUserEventDtoToUserModel(UserEventDTO userEventDTO) {
        var userModel = new UserModel();
        userModel.setUserId(userEventDTO.userId());
        userModel.setEmail(userEventDTO.email());
        userModel.setFullName(userEventDTO.fullName());
        userModel.setUserStatus(userEventDTO.userStatus());
        userModel.setUserType(userEventDTO.userType());
        userModel.setImageUrl(userEventDTO.imageUrl());
        return userModel;
    }

}
