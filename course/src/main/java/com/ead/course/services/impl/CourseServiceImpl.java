package com.ead.course.services.impl;

import com.ead.course.configs.exceptions.ConflictException;
import com.ead.course.configs.exceptions.NotFoundException;
import com.ead.course.dto.CourseDTO;
import com.ead.course.dto.NotificationCommandDTO;
import com.ead.course.enums.UserStatusEnum;
import com.ead.course.enums.UserTypeEnum;
import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;
import com.ead.course.publishers.NotificationCommandPublischer;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    Logger logger = LogManager.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final ModuleService moduleService;
    private final UserService userService;
    private final NotificationCommandPublischer notificationCommandPublischer;

    public CourseServiceImpl(CourseRepository courseRepository, ModuleService moduleService, UserService userService, NotificationCommandPublischer notificationCommandPublischer) {
        this.courseRepository = courseRepository;
        this.moduleService = moduleService;
        this.userService = userService;
        this.notificationCommandPublischer = notificationCommandPublischer;
    }

    @Transactional
    @Override
    public void deleteCourse(UUID courseId) {
        CourseModel courseModel = findById(courseId);
        moduleService.deleteAllByCourse(courseId);
        courseRepository.deleteSubscriptionsByCourseId(courseId);
        courseRepository.delete(courseModel);
    }

    @Override
    public CourseModel findById(UUID courseId) {
        return courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found."));
    }

    @Override
    public CourseModel saveCourse(CourseDTO courseDto) {
        if(courseRepository.existsByName(courseDto.name())) throw new ConflictException("Error: Course Name already exists.");
        UserModel userModel = userService.findById(courseDto.userInstructor());
        if (!UserTypeEnum.INSTRUCTOR.name().equalsIgnoreCase(userModel.getUserType()) && !UserTypeEnum.ADMIN.name().equalsIgnoreCase(userModel.getUserType())) {
            throw new ConflictException("User must be an INSTRUCTOR or ADMIN.");
        }

        CourseModel courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDto, courseModel);
        courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return courseRepository.save(courseModel);
    }

    @Override
    public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable) {
        return courseRepository.findAll(spec, pageable);
    }

    @Override
    public CourseModel updateCourse(UUID courseId, CourseDTO courseDto) {
        CourseModel courseModel = findById(courseId);
        BeanUtils.copyProperties(courseDto, courseModel);
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return courseRepository.save(courseModel);
    }

    @Override
    public boolean existsByCourseId(UUID courseId) {
        return courseRepository.existsById(courseId);
    }

    @Override
    @Transactional
    public void saveSubscriptionUserInCourse(UUID courseId, UUID userId) {
        CourseModel courseModel = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found."));
        if (courseRepository.existsByCourseIdAndUsersUserId(courseId, userId)) {
            throw new ConflictException("Error: subscription already exists!");
        }
        UserModel userModel = userService.findById(userId);

        if(UserStatusEnum.BLOCKED.name().equalsIgnoreCase(userModel.getUserStatus())) throw new ConflictException("User is blocked.");

        courseRepository.saveSubscriptionUserInCourse(courseModel.getCourseId(), userModel.getUserId());

        enviaMensagemNotificacao(courseModel, userModel);

    }

    private void enviaMensagemNotificacao(CourseModel courseModel, UserModel userModel) {
        try {
            NotificationCommandDTO notificationCommandDTO = new NotificationCommandDTO(
                    "Bem-Vindo(a) ao curso: " + courseModel.getName(),
                    userModel.getFullName() + " a sua inscrição foi realizada com sucesso!",
                    userModel.getUserId());
            notificationCommandPublischer.publishNotificationCommand(notificationCommandDTO);
        } catch (Exception e) {
            logger.error("Error sending notification!");
        }
    }
}
