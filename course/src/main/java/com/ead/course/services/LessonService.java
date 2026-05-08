package com.ead.course.services;

import com.ead.course.dto.LessonDTO;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface LessonService {
    void deleteLessonsByModule(ModuleModel moduleModel);

    LessonModel saveLessonIntoModule(LessonDTO lessonDto, UUID moduleId);

    List<LessonModel> findAllLessonsByModuleId(UUID moduleId);
    Page<LessonModel> findAllLessonsByModuleId(Specification<LessonModel> spec, Pageable pageable);

    LessonModel getOneLessonByModuleId(UUID moduleId, UUID lessonId);

    void deleteAllLessonsByModules(List<ModuleModel> moduleModels);

    void deleteLessonInsideAModule(UUID moduleId, UUID lessonId);

    LessonModel updateLessonInsideAModule(UUID moduleId, UUID lessonId, LessonDTO lessonDto);
}