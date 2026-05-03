package com.ead.course.services.impl;

import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final LessonService lessonService;

    public ModuleServiceImpl(ModuleRepository moduleRepository, LessonService lessonService) {
        this.moduleRepository = moduleRepository;
        this.lessonService = lessonService;
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

}