package com.ead.authuser.services;

import com.ead.authuser.dto.UserDTORequest;
import com.ead.authuser.enums.UserTypeEnum;
import com.ead.authuser.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserModel> findAll();

    UserModel findById(UUID userId);

    void delete(UserModel userModel);

    UserModel registerUser(UserDTORequest userDTORequest);

    UserModel updateUser(UserDTORequest userDTORequest, UserModel byId);

    UserModel updatePassword(UserDTORequest userDTORequest, UserModel userModel);

    UserModel updateImage(UserDTORequest userDTORequest, UserModel byId);

    Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable);

    void updateUserType(UUID userId, UserTypeEnum userTypeEnum);

    Boolean existsByUserId(UUID userId);
}
