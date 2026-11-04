package com.ead.course.services.impl;

import com.ead.course.configs.exceptions.NotFoundException;
import com.ead.course.dto.UserEventDTO;
import com.ead.course.models.UserModel;
import com.ead.course.repositories.UserRepository;
import com.ead.course.services.UserService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserModel> getAllUsersByCourse(Specification<UserModel> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }

    @Transactional
    @Override
    public UserModel saveUser(UserEventDTO userEventDTO) {
        logger.info("Saving or updating user from event with id: {}", userEventDTO.userId());
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userEventDTO, userModel);
        UserModel savedUser = userRepository.save(userModel);
        logger.info("User saved successfully with id: {}", savedUser.getUserId());
        return savedUser;
    }

    @Transactional
    @Override
    public void deleteUserById(UUID userId) {
        logger.info("Deleting user and subscriptions for userId: {}", userId);
        userRepository.deleteSubscriptionsByUserId(userId);
        userRepository.deleteById(userId);
        logger.info("User with id {} deleted successfully.", userId);
    }

    @Override
    public UserModel findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
    }

}
