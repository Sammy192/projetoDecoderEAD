package com.ead.course.dto;

import com.ead.course.enums.CourseLevel;
import com.ead.course.enums.CourseStatus;
import com.ead.course.models.CourseModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseResponseDTO(
        UUID courseId,
        String name,
        String description,
        String imageUrl,
        CourseStatus courseStatus,
        CourseLevel courseLevel,
        UUID userInstructor,
        LocalDateTime creationDate,
        LocalDateTime lastUpdateDate
) {
    public CourseResponseDTO(CourseModel model) {
        this(
                model.getCourseId(),
                model.getName(),
                model.getDescription(),
                model.getImageUrl(),
                model.getCourseStatus(),
                model.getCourseLevel(),
                model.getUserInstructor(),
                model.getCreationDate(),
                model.getLastUpdateDate()
        );
    }
}