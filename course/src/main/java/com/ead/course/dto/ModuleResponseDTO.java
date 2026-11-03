package com.ead.course.dto;

import com.ead.course.models.ModuleModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record ModuleResponseDTO(
        UUID moduleId,
        String title,
        String description,
        LocalDateTime creationDate
) {
    public ModuleResponseDTO(ModuleModel model) {
        this(
                model.getModuleId(),
                model.getTitle(),
                model.getDescription(),
                model.getCreationDate()
        );
    }
}