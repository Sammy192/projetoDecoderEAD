package com.ead.authuser.services;

import com.ead.authuser.dto.UserDTORequest;
import com.ead.authuser.models.UserModel;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserModel> findAll();

    UserModel findById(UUID userId);

    void delete(UserModel userModel);

    UserModel registerUser(UserDTORequest userDTORequest);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserModel updateUser(UserDTORequest userDTORequest, UserModel byId);

    UserModel updatePassword(UserDTORequest userDTORequest, UserModel userModel);

    UserModel updateImage(UserDTORequest userDTORequest, UserModel byId);
}
