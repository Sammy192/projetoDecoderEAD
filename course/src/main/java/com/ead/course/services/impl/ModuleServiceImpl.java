package com.ead.course.services.impl;

import com.ead.course.configs.exceptions.ResourceNotFoundException;
import com.ead.course.dto.ModuleDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final LessonService lessonService;
    private final CourseRepository courseRepository;

    public ModuleServiceImpl(ModuleRepository moduleRepository, LessonService lessonService, CourseRepository courseRepository) {
        this.moduleRepository = moduleRepository;
        this.lessonService = lessonService;
        this.courseRepository = courseRepository;
    }

    @Transactional
    @Override
    public void deleteModule(ModuleModel moduleModel) {
        lessonService.deleteLessonsByModule(moduleModel);
        moduleRepository.delete(moduleModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleModel> findAllModulesByCourse(CourseModel courseModel) {
        return moduleRepository.findAllByCourseCourseId(courseModel.getCourseId());
    }

    @Override
    public ModuleModel saveModule(ModuleDTO moduleDto, UUID courseId) {
        CourseModel courseModel = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found."));
        ModuleModel moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDto, moduleModel);
        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        moduleModel.setCourse(courseModel);
        return moduleRepository.save(moduleModel);
    }

}