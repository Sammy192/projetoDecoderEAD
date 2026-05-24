package com.ead.authuser.services.impl;

import com.ead.authuser.clients.CourseClient;
import com.ead.authuser.configs.exceptions.ConflictException;
import com.ead.authuser.configs.exceptions.NotFoundException;
import com.ead.authuser.enums.UserStatusEnum;
import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.repositories.UserCourseRepository;
import com.ead.authuser.services.UserCourseService;
import com.ead.authuser.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserCourseServiceImpl implements UserCourseService {

    private final UserCourseRepository userCourseRepository;
    private final UserService userService;
    private final CourseClient courseClient;

    public UserCourseServiceImpl(UserCourseRepository userCourseRepository, UserService userService, CourseClient courseClient) {
        this.userCourseRepository = userCourseRepository;
        this.userService = userService;
        this.courseClient = courseClient;
    }

    @Override
    @Transactional
    public UserCourseModel saveSubscriptionUserInCourse(UUID userId, UUID courseId) {
        UserModel userModel = userService.findById(userId);
        if(userModel.getUserStatus().equals(UserStatusEnum.BLOCKED)) throw new ConflictException("User is blocked.");
        if (userCourseRepository.existsByUserAndCourseId(userModel, courseId)) {
            throw new ConflictException("Error: subscription already exists!");
        }

        courseClient.getCourseById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));

        UserCourseModel userCourseModel = new UserCourseModel();
        userCourseModel.setCourseId(courseId);
        userCourseModel.setUser(userModel);
        return userCourseRepository.save(userCourseModel);
    }

}
