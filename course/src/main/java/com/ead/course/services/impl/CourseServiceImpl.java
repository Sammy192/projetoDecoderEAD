package com.ead.course.services.impl;

import com.ead.course.configs.exceptions.ResourceNotFoundException;
import com.ead.course.dto.CourseDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModuleService moduleService;

    public CourseServiceImpl(CourseRepository courseRepository, ModuleService moduleService) {
        this.courseRepository = courseRepository;
        this.moduleService = moduleService;
    }

    @Transactional
    @Override
    public void deleteCourse(CourseModel course) {
        CourseModel courseModel = findById(course.getCourseId());
        List<ModuleModel> moduleModelList = moduleService.findAllModulesByCourse(courseModel);
        if (!moduleModelList.isEmpty()) {
            moduleModelList.forEach(moduleService::deleteModule);
        }
        courseRepository.delete(courseModel);
    }

    @Override
    public CourseModel findById(UUID courseId) {
        return courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found."));
    }

    @Override
    public CourseModel saveCourse(CourseDTO courseDto) {
        CourseModel courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDto, courseModel);
        courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return courseRepository.save(courseModel);
    }

    @Override
    public boolean existsByName(String name) {
        return courseRepository.existsByName(name);
    }
}
