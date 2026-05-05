package com.ead.course.services;

import com.ead.course.dto.ModuleDTO;
import com.ead.course.models.ModuleModel;

import java.util.List;
import java.util.UUID;

public interface ModuleService {
    void deleteModule(ModuleModel moduleModel);

    ModuleModel saveModule(ModuleDTO moduleDto, UUID courseId);

    List<ModuleModel> findAllModulesByCourseId(UUID courseId);

    ModuleModel findModuleByIdIntoCourse(UUID courseId, UUID moduleId);

    ModuleModel updateModule(UUID courseId, UUID moduleId, ModuleDTO moduleDTO);
}