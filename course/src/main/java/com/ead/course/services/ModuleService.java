package com.ead.course.services;

import com.ead.course.dto.ModuleDTO;
import com.ead.course.models.ModuleModel;

import java.util.List;
import java.util.UUID;

public interface ModuleService {
    void deleteModuleInsideACourse(UUID courseId, UUID moduleId);

    ModuleModel saveModuleIntoCourse(ModuleDTO moduleDto, UUID courseId);

    List<ModuleModel> findAllModulesByCourseId(UUID courseId);

    ModuleModel findModuleByIdIntoCourse(UUID courseId, UUID moduleId);

    ModuleModel updateModuleInsideACourse(UUID courseId, UUID moduleId, ModuleDTO moduleDTO);

    void deleteAllByCourse(UUID courseId);
}