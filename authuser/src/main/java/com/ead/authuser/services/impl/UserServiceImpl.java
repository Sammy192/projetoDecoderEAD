package com.ead.authuser.services.impl;

import com.ead.authuser.clients.CourseClient;
import com.ead.authuser.configs.exceptions.ConflictException;
import com.ead.authuser.configs.exceptions.NotFoundException;
import com.ead.authuser.dto.UserDTORequest;
import com.ead.authuser.enums.UserStatusEnum;
import com.ead.authuser.enums.UserTypeEnum;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.repositories.UserCourseRepository;
import com.ead.authuser.repositories.UserRepository;
import com.ead.authuser.services.UserService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;
    private final CourseClient courseClient;

    public UserServiceImpl(UserRepository userRepository, UserCourseRepository userCourseRepository, CourseClient courseClient) {
        this.userRepository = userRepository;
        this.userCourseRepository = userCourseRepository;
        this.courseClient = courseClient;
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
    @Transactional
    public void delete(UserModel userModel) {
        userCourseRepository.deleteAllByUserUserId(userModel.getUserId());
        userRepository.delete(userModel);
        courseClient.deleteUserInCourse(userModel.getUserId());
    }

    @Override
    public UserModel registerUser(UserDTORequest userDTORequest) {
        if(userRepository.existsByUsername(userDTORequest.username())){
            logger.warn("Username {} is already taken!", userDTORequest.username());
            throw new ConflictException("Error: Username já existe.");

        }
        if(userRepository.existsByEmail(userDTORequest.email())){
            logger.warn("Email {} is already taken!", userDTORequest.email());
            throw new ConflictException("Error: Email já existe.");
        }

        var userModel = new UserModel();
        BeanUtils.copyProperties(userDTORequest, userModel);
        userModel.setUserStatus(UserStatusEnum.ACTIVE);
        userModel.setUserType(UserTypeEnum.USER);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return userRepository.save(userModel);
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

    @Override
    public void promoteToInstructor(UUID userId) {
        UserModel userModel = findById(userId);
        if (UserTypeEnum.INSTRUCTOR.equals(userModel.getUserType())) return;
        userModel.setUserType(UserTypeEnum.INSTRUCTOR);
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userRepository.save(userModel);
    }

    @Override
    public Boolean existsByUserId(UUID userId) {
        return userRepository.existsById(userId);
    }
}
