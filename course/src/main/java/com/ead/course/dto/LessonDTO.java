package com.ead.course.dto;

import jakarta.validation.constraints.NotBlank;

public record LessonDTO(@NotBlank(message = "Title is mandatory")
                        String title,

                        @NotBlank(message = "Description is mandatory")
                        String description,

                        @NotBlank(message = "Video URL is mandatory")
                        String videoUrl
) {
}
