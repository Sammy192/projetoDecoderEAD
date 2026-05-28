package com.ead.course.services.impl;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.configs.exceptions.ConflictException;
import com.ead.course.configs.exceptions.NotFoundException;
import com.ead.course.dto.UserDTO;
import com.ead.course.enums.UserStatusEnum;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CourseUserServiceImpl implements CourseUserService {

    private final CourseUserRepository courseUserRepository;
    private final CourseService courseService;
    private final AuthUserClient authUserClient;

    public CourseUserServiceImpl(CourseUserRepository courseUserRepository, CourseService courseService, AuthUserClient authUserClient) {
        this.courseUserRepository = courseUserRepository;
        this.courseService = courseService;
        this.authUserClient = authUserClient;
    }

    @Override
    @Transactional
    public CourseUserModel saveSubscriptionUserInCourse(UUID courseId, UUID userId) {
        CourseModel courseModel = courseService.findById(courseId);
        if (courseUserRepository.existsByCourseAndUserId(courseModel, userId)) {
            throw new ConflictException("Error: subscription already exists!");
        }
        UserDTO userDTO = authUserClient.getUserById(userId).orElseThrow(() -> new NotFoundException("User not found."));

        if(userDTO.userStatus().equals(UserStatusEnum.BLOCKED)) throw new ConflictException("User is blocked.");

        CourseUserModel courseUserModel = new CourseUserModel();
        courseUserModel.setCourse(courseModel);
        courseUserModel.setUserId(userId);
        CourseUserModel courseUserModelSaved = courseUserRepository.save(courseUserModel);

        authUserClient.postSubscriptionUserInCourse(courseId, userId);

        return courseUserModelSaved;
    }

    @Override
    public Page<UserDTO> getAllUsersByCourse(UUID courseId, Pageable pageable) {
        if(!courseService.existsByCourseId(courseId)) throw new NotFoundException("Course not found.");
        return authUserClient.getAllUsersByCourse(courseId, pageable);
    }

    @Override
    @Transactional
    public void deleteCourseUserByUserId(UUID userId) {
        if(!courseUserRepository.existsByUserId(userId)) throw new NotFoundException("Any CourseUser register for this userId was found.");
        courseUserRepository.deleteAllByUserId(userId);
    }
}
