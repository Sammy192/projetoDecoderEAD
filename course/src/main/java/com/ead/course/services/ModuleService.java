package com.ead.course.services;

import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;

import java.util.List;

public interface ModuleService {
    void deleteModule(ModuleModel moduleModel);
    List<ModuleModel> findAllModulesByCourse(CourseModel courseModel);
}