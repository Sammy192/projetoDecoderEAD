package com.ead.course.services;

import com.ead.course.dto.ModuleDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;

import java.util.List;
import java.util.UUID;

public interface ModuleService {
    void deleteModule(ModuleModel moduleModel);
    List<ModuleModel> findAllModulesByCourse(CourseModel courseModel);

    ModuleModel saveModule(ModuleDTO moduleDto, UUID courseId);

    List<ModuleModel> findAllModulesByCourseId(UUID courseId);
}