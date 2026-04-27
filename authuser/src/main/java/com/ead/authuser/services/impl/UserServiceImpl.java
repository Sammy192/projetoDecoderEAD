package com.ead.authuser.services.impl;

import com.ead.authuser.dto.UserDTORequest;
import com.ead.authuser.enums.UserStatusEnum;
import com.ead.authuser.enums.UserTypeEnum;
import com.ead.authuser.configs.exceptions.NotFoundException;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.repositories.UserRepository;
import com.ead.authuser.services.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserModel findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Error: User not found"));
    }

    @Override
    public void delete(UserModel userModel) {
        userRepository.delete(userModel);
    }

    @Override
    public UserModel registerUser(UserDTORequest userDTORequest) {
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDTORequest, userModel);
        userModel.setUserStatus(UserStatusEnum.ACTIVE);
        userModel.setUserType(UserTypeEnum.USER);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return userRepository.save(userModel);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserModel updateUser(UserDTORequest userDTORequest, UserModel userModel) {
        userModel.setFullName(userDTORequest.fullName());
        userModel.setPhoneNumber(userDTORequest.phoneNumber());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return userRepository.save(userModel);
    }

    @Override
    public UserModel updatePassword(UserDTORequest userDTORequest, UserModel userModel) {
        userModel.setPassword(userDTORequest.password());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return userRepository.save(userModel);
    }

    @Override
    public UserModel updateImage(UserDTORequest userDTORequest, UserModel byId) {
        byId.setImageUrl(userDTORequest.imageUrl());
        byId.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return userRepository.save(byId);
    }

    @Override
    public Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }
}
