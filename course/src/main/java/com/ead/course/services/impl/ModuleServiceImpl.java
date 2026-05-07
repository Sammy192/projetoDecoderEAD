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
    public void deleteModuleInsideACourse(UUID courseId, UUID moduleId) {
        if (!courseRepository.existsById(courseId)) throw new ResourceNotFoundException("Course not found.");
        deleteOneModule(moduleId);
    }

    private void deleteOneModule(UUID moduleId) {
        ModuleModel moduleModel = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found."));
        lessonService.deleteLessonsByModule(moduleModel);
        moduleRepository.delete(moduleModel);
    }

    @Transactional
    @Override
    public ModuleModel saveModuleIntoCourse(ModuleDTO moduleDto, UUID courseId) {
        CourseModel courseModel = getCourseModel(courseId);
        ModuleModel moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDto, moduleModel);
        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        moduleModel.setCourse(courseModel);
        return moduleRepository.save(moduleModel);
    }

    private CourseModel getCourseModel(UUID courseId) {
        return courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleModel> findAllModulesByCourseId(UUID courseId) {
        if (!courseRepository.existsById(courseId)) throw new ResourceNotFoundException("Course not found.");
        return moduleRepository.findAllByCourseCourseId(courseId);
    }

    @Override
    public ModuleModel findModuleByIdIntoCourse(UUID courseId, UUID moduleId) {
        if (!courseRepository.existsById(courseId)) throw new ResourceNotFoundException("Course not found.");
        return moduleRepository.findByCourseCourseIdAndModuleId(courseId, moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found for this course."));
    }

    @Override
    public ModuleModel updateModuleInsideACourse(UUID courseId, UUID moduleId, ModuleDTO moduleDTO) {
        ModuleModel moduleModel = findModuleByIdIntoCourse(courseId, moduleId);
        BeanUtils.copyProperties(moduleDTO, moduleModel);
        return moduleRepository.save(moduleModel);
    }

    @Transactional
    @Override
    public void deleteAllByCourse(UUID courseId) {
        List<ModuleModel> modules = moduleRepository.findAllByCourseCourseId(courseId);
        if (!modules.isEmpty()) {
            lessonService.deleteAllLessonsByModules(modules);
            moduleRepository.deleteAllInBatch(modules);
        }
    }

}