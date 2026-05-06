package com.ead.course.services;

import com.ead.course.dto.LessonDTO;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import jakarta.validation.Valid;

import java.util.UUID;

public interface LessonService {
    void deleteLessonsByModule(ModuleModel moduleModel);

    LessonModel saveLesson(LessonDTO lessonDto, UUID moduleId);
}