package com.ead.course.dto;

import com.ead.course.models.LessonModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record LessonResponseDTO(
        UUID lessonId,
        String title,
        String description,
        String videoUrl,
        LocalDateTime creationDate
) {
    public LessonResponseDTO(LessonModel model) {
        this(
                model.getLessonId(),
                model.getTitle(),
                model.getDescription(),
                model.getVideoUrl(),
                model.getCreationDate()
        );
    }
}