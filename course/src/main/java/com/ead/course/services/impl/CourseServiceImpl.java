package com.ead.course.services.impl;

import com.ead.course.configs.exceptions.ConflictException;
import com.ead.course.configs.exceptions.NotFoundException;
import com.ead.course.dto.CourseDTO;
import com.ead.course.enums.UserTypeEnum;
import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.services.UserService;
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

    private final CourseRepository courseRepository;
    private final ModuleService moduleService;
    private final UserService userService;

    public CourseServiceImpl(CourseRepository courseRepository, ModuleService moduleService, UserService userService) {
        this.courseRepository = courseRepository;
        this.moduleService = moduleService;
        this.userService = userService;
    }

    @Transactional
    @Override
    public void deleteCourse(UUID courseId) {
        CourseModel courseModel = findById(courseId);
        moduleService.deleteAllByCourse(courseId);
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
        if (!UserTypeEnum.INSTRUCTOR.name().equals(userModel.getUserType()) && !UserTypeEnum.ADMIN.name().equals(userModel.getUserType())) {
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

}
